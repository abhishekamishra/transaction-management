package com.infy.transaction_management.serviceImpl;

import com.infy.transaction_management.dto.CustomerDto;
import com.infy.transaction_management.dto.MonthlyAmountDto;
import com.infy.transaction_management.dto.TransactionDetailsDto;
import com.infy.transaction_management.entity.Customer;
import com.infy.transaction_management.entity.Transaction;
import com.infy.transaction_management.repository.CustomerRepository;
import com.infy.transaction_management.service.CustomerService;
import com.infy.transaction_management.util.CustomerUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public Optional<List<CustomerDto>> saveCustomerDetails(List<CustomerDto> customerDtos) {
        List<CustomerDto> customerDtoList = new ArrayList<>();
        if (!customerDtos.isEmpty()) {
            try {
                // saving customer details to database
                List<Customer> customerDetails = customerRepository.saveAllAndFlush(CustomerUtil.createCustomer(customerDtos));
                // converting customer objects into customerdtos
                customerDtoList = customerDetails.stream().map(cust -> new CustomerDto(cust.getId() == null ? null : cust.getId(), cust.getCustomerName() == null ? null : cust.getCustomerName(), cust.getCustomerId() == null ? null : cust.getCustomerId(), cust.getTransactions() == null ? null : CustomerUtil.getTransactionsDto(cust.getTransactions()))).toList();
            } catch (Exception exception) {
                log.error("Error occurred while saving customer details to database as {}", exception.getLocalizedMessage());
                exception.printStackTrace();
            }
        }
        return Optional.of(customerDtoList);
    }

    @Override
    public Optional<TransactionDetailsDto> calculateDiscountPoints(Long customerId) {

        Optional<Customer> customer = validateCustomer(customerId);
        if (customer.isPresent()) {
            TransactionDetailsDto transactionDetailsDto = new TransactionDetailsDto();
            transactionDetailsDto.setCustomerId(customer.get().getCustomerId() == null ? null : customer.get().getCustomerId());
            transactionDetailsDto.setCustomerName(customer.get().getCustomerName() == null ? null : customer.get().getCustomerName());

            List<MonthlyAmountDto> monthlyAmounts = calculateMonthlyAmounts(customer);

            transactionDetailsDto.setMonthlyDetails(monthlyAmounts.size() == 0 ? null : monthlyAmounts);
            transactionDetailsDto.setSumOfQurterlyRewardPoints(monthlyAmounts.size() == 0 ? null : calculateQuarterlyDiscountPoints(monthlyAmounts));

            log.info("TransactionDetailsDto: {}", transactionDetailsDto);
            return Optional.of(transactionDetailsDto);
        }
        log.info("Customer details for customer id {} is not present. So could not proceed with rewards points calculation.", customerId);
        return Optional.empty();
    }

    private List<MonthlyAmountDto> calculateMonthlyAmounts(Optional<Customer> customer) {
        List<MonthlyAmountDto> monthlyAmountList = new ArrayList<>();

        try {
            if (customer.isPresent() && !customer.get().getTransactions().isEmpty()) {

                for (Transaction transaction : customer.get().getTransactions()) {
                    monthlyAmountList.add(CustomerUtil.createMonthlyAmountDto(calculateMonthlyDiscountPoints(transaction), transaction));
                }
            }
        } catch (Exception exception) {
            log.error("Error occurred for calculateMonthlyAmounts method as {}", exception.getLocalizedMessage());
            exception.printStackTrace();
        }
        return monthlyAmountList;
    }

    private Optional<Customer> validateCustomer(Long customerId) {

        if (customerId == null) {
            log.info("Customer id is null. Please provide a customer id.");
            return Optional.empty();
        }
        log.info("Calculation of discount points started for customer id: " + customerId);

        Optional<Customer> customer = Optional.empty();
        try {
            customer = customerRepository.findByCustomerId(customerId);
            if (customer.isPresent()) {
                log.debug("Customer details: {}", customer);
            } else {
                log.info("Customer details for customer id {} is not present", customerId);
            }
        } catch (Exception exception) {
            log.error("Error occurred while fetching customer details with customer id: " + customerId + " as " + exception.getLocalizedMessage());
            exception.printStackTrace();
        }
        return customer;
    }

    /***
     * Quarterly discount points calculated
     */
    private Long calculateQuarterlyDiscountPoints(List<MonthlyAmountDto> monthlyAmounts) {

        Long qurterlyAmount = 0L;
        for (MonthlyAmountDto monthlyAmountDto : monthlyAmounts) {
            if (null != monthlyAmountDto) {
                qurterlyAmount = qurterlyAmount + monthlyAmountDto.getRewardPoints();
            } else {
                log.info("MonthlyAmountDto is not available.");
            }
        }
        return qurterlyAmount;
    }

    private Optional<Long> calculateMonthlyDiscountPoints(Transaction transaction) {

        long monthlyDiscountPoints = 0L;
        try {
            if (null != transaction) {
                Double amount = transaction.getAmount();
                // ex: $120 = 2*$20 +1*$50 = 90 pts
                if (null != amount && amount != 0.00) {
                    // 1 point for every dollar spent between $50 and $100 in each transaction.
                    if (amount > 50.00 && amount <= 100.00) {
                        monthlyDiscountPoints = (long) (Math.floor(amount - 50.00));
                    } // A customer receives 2 points for every dollar spent over $100 in each transaction
                    else if (amount > 100.00) {
                        monthlyDiscountPoints = (long) Double.sum(50.00, (Math.floor(amount - 100.00)) * 2);
                    }
                } else {
                    log.info("Transaction amount is zero or null for customer id {} of transaction id {}", transaction.getCustomer().getCustomerId(), transaction.getId());
                }
            } else {
                log.info("Transaction details is not available.");
            }
        } catch (Exception exception) {
            log.error("Error occurred while calculating monthly discount points");
            exception.printStackTrace();
        }
        log.debug("Monthly discount points: {}", monthlyDiscountPoints);
        return Optional.of(monthlyDiscountPoints);
    }
}
