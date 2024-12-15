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

    /**
     * This method saves customers details to database
     *
     * @param customerDtos - customerDto objects containing user provided data
     * @return saved customer details
     */
    @Override
    public Optional<List<CustomerDto>> saveCustomerDetails(List<CustomerDto> customerDtos) {
        List<CustomerDto> customerDtoList = new ArrayList<>();
        if (!customerDtos.isEmpty()) {
            try {
                // saving customer details to database
                List<Customer> customerDetails = customerRepository.saveAllAndFlush(CustomerUtil.createCustomer(customerDtos));
                // converting customer objects into customerdto objects
                customerDtoList = customerDetails.stream().map(cust -> new CustomerDto(cust.getId() == null ? null : cust.getId(), cust.getCustomerName() == null ? null : cust.getCustomerName(), cust.getCustomerId() == null ? null : cust.getCustomerId(), cust.getTransactions() == null ? null : CustomerUtil.getTransactionsDto(cust.getTransactions()))).toList();
            } catch (Exception exception) {
                log.error("Error occurred while saving customer details to database as {}", exception.getLocalizedMessage());
                exception.printStackTrace();
            }
        }
        return Optional.of(customerDtoList);
    }

    /**
     * This method calculates discountPoints
     *
     * @param customerId - user provided customer id
     * @return TransactionDetailsDto object - It has customer details, monthly reward points and quarterly reward points
     */
    @Override
    public Optional<TransactionDetailsDto> calculateDiscountPoints(Long customerId) {

        Optional<Customer> customer = validateCustomer(customerId);

        if (customer.isPresent()) {

            TransactionDetailsDto transactionDetailsDto = new TransactionDetailsDto();
            transactionDetailsDto.setCustomerId(customer.get().getCustomerId() == null ? null : customer.get().getCustomerId());
            transactionDetailsDto.setCustomerName(customer.get().getCustomerName() == null ? null : customer.get().getCustomerName());

            List<MonthlyAmountDto> monthlyAmounts = calculateMonthlyAmounts(customer);

            transactionDetailsDto.setMonthlyDetails(monthlyAmounts.isEmpty() ? null : monthlyAmounts);
            transactionDetailsDto.setSumOfQurterlyRewardPoints(monthlyAmounts.isEmpty() ? null : calculateQuarterlyDiscountPoints(monthlyAmounts));

            log.info("TransactionDetailsDto: {}", transactionDetailsDto);
            return Optional.of(transactionDetailsDto);
        }

        log.info("Customer details for customer id {} is not present. So could not proceed with rewards points calculation.", customerId);
        return Optional.empty();
    }

    /**
     * This method creates a list of monthly amounts and reward points(MonthlyAmountDto) per month basis
     *
     * @param customer - customer object
     * @return List of MonthlyAmountDto objects
     */
    private List<MonthlyAmountDto> calculateMonthlyAmounts(Optional<Customer> customer) {

        List<MonthlyAmountDto> monthlyAmountList = new ArrayList<>();

        try {
            if (customer.isPresent() && !customer.get().getTransactions().isEmpty()) {

                // iterates list of transactions per customer
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

    /**
     * This method validates if customer is available or not based on the customer id
     *
     * @param customerId - customer id
     * @return Customer object
     */
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

    /**
     * This method calculates quarterly discount points.
     * i.e.: quarterly discount points = sum of three months discount points
     *
     * @param monthlyAmounts - list of MonthlyAmountDto object
     * @return sum of quarterly reward points
     */
    private Long calculateQuarterlyDiscountPoints(List<MonthlyAmountDto> monthlyAmounts) {

        long sumOfQurterlyRewardPoints = 0L;

        // iterating monthly reward points
        for (MonthlyAmountDto monthlyAmountDto : monthlyAmounts) {
            if (null != monthlyAmountDto) {
                // quarterly discount points = sum of three months discount points
                sumOfQurterlyRewardPoints = sumOfQurterlyRewardPoints + monthlyAmountDto.getRewardPoints();
            } else {
                log.info("MonthlyAmountDto is not available.");
            }
        }

        return sumOfQurterlyRewardPoints;
    }

    /**
     * This method calculates monthly discount points based on the transaction amounts per month
     *
     * @param transaction - Transaction object
     * @return monthly discount points
     */
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
