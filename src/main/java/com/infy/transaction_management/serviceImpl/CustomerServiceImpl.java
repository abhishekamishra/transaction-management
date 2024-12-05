package com.infy.transaction_management.serviceImpl;

import com.infy.transaction_management.dto.CustomerDto;
import com.infy.transaction_management.dto.MonthlyAmountDto;
import com.infy.transaction_management.dto.TransactionDetailsDto;
import com.infy.transaction_management.dto.TransactionDto;
import com.infy.transaction_management.entity.Customer;
import com.infy.transaction_management.entity.Transaction;
import com.infy.transaction_management.exception.CalculateDiscountPointsException;
import com.infy.transaction_management.exception.CustomerNotFoundException;
import com.infy.transaction_management.exception.MonthlyDiscountPointsArithmeticException;
import com.infy.transaction_management.exception.MonthlyDiscountPointsException;
import com.infy.transaction_management.repository.CustomerRepository;
import com.infy.transaction_management.service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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
                List<Customer> customerDetails = customerRepository.saveAllAndFlush(createCustomer(customerDtos));
                // converting customer objects into customerdtos
                customerDtoList = customerDetails.stream().map(cust -> new CustomerDto(cust.getId() == null ? null : cust.getId(), cust.getCustomerName() == null ? null : cust.getCustomerName(), cust.getCustomerId() == null ? null : cust.getCustomerId(), cust.getTransactions() == null ? null : getTransactionsDto(cust.getTransactions()))).toList();
            } catch (Exception exception) {
                exception.printStackTrace();
                log.error("Error occurred while saving customer details to database as {}", exception.getLocalizedMessage());
            }
        }
        return Optional.of(customerDtoList);
    }

    private List<Customer> createCustomer(List<CustomerDto> customerDtos) {

        List<Customer> customers = new ArrayList<>();
        for (CustomerDto customerDto : customerDtos) {
            Customer customer = new Customer();
            customer.setCustomerId(Double.valueOf(generateRandomNumber()).longValue());
            customer.setCustomerName(customerDto.getCustomerName() == null ? null : customerDto.getCustomerName());
            customer.setTransactions(createTransaction(customerDto.getTransactions(), customer));
            customers.add(customer);
        }
        return customers;
    }

    private LinkedHashSet<Transaction> createTransaction(Set<TransactionDto> transactionDtos, Customer customer) {

        LinkedHashSet<Transaction> transactions = new LinkedHashSet<>();
        for (TransactionDto transactionDto : transactionDtos) {
            Transaction transaction = new Transaction();
            transaction.setMonth(transactionDto.getMonth() == null ? null : transactionDto.getMonth());
            transaction.setAmount(transactionDto.getAmount() == null ? null : transactionDto.getAmount());
            transaction.setCustomer(customer);
            transactions.add(transaction);
        }
        return transactions;
    }

    public double generateRandomNumber() {
        double max = Math.pow(10, 3) - 1;
        double min = Math.pow(10, 1 - 1);
        double range = max - min + 1;
        return (int) (Math.random() * range) + min;
    }

    private LinkedHashSet<TransactionDto> getTransactionsDto(Set<Transaction> transactions) {
        return transactions.stream().map(t -> new TransactionDto(t.getId() == null ? null : t.getId(), t.getMonth() == null ? null : t.getMonth(), t.getAmount() == null ? null : t.getAmount(), t.getCustomer() == null ? null : t.getCustomer().getCustomerId())).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public Optional<TransactionDetailsDto> calculateDiscountPoints(Long customerId) {

        Optional<Customer> customer = validateCustomer(customerId);
        if (customer.isPresent()) {
            List<MonthlyAmountDto> monthlyAmounts = calculateMonthlyAmounts(customer);

            TransactionDetailsDto transactionDetailsDto = new TransactionDetailsDto();
            transactionDetailsDto.setCustomerId(customer.get().getCustomerId());
            transactionDetailsDto.setCustomerName(customer.get().getCustomerName());
            transactionDetailsDto.setMonthlyDetails(monthlyAmounts);
            transactionDetailsDto.setQurterlyRewardPoints(calculateQuarterlyDiscountPoints(monthlyAmounts) == null ? null : calculateQuarterlyDiscountPoints(monthlyAmounts));

            log.debug("TransactionDetailsDto: {}", transactionDetailsDto);
            return Optional.of(transactionDetailsDto);
        }
        return Optional.empty();
    }

    private List<MonthlyAmountDto> calculateMonthlyAmounts(Optional<Customer> customer) {
        List<MonthlyAmountDto> monthlyAmountList = new ArrayList<>();
        MonthlyAmountDto monthlyAmountDto = null;
        try {
            Long monthlyRewardPoints;
            int flag = 1;
            if (customer.isPresent() && !customer.get().getTransactions().isEmpty()) {
                // sorting transactions details based on id
                Set<Transaction> transactions = customer.get().getTransactions().stream().sorted(Comparator.comparing(Transaction::getId)).collect(Collectors.toCollection(LinkedHashSet::new));
                for (Transaction transaction : transactions) {
                    monthlyAmountDto = new MonthlyAmountDto();
                    if (flag > 3) {
                        continue;
                    }
                    Optional<Long> monthlyAmountValue = calculateMonthlyDiscountPoints(transaction);
                    monthlyRewardPoints = monthlyAmountValue.isPresent() ? monthlyAmountValue.get() : 0l;
                    // Monthly amount calculated
                    if (monthlyRewardPoints != 0l) {
                        monthlyAmountDto.setMonth(transaction.getMonth());
                        monthlyAmountDto.setRewardPoints(monthlyRewardPoints);
                        monthlyAmountDto.setAmount(transaction.getAmount() == null ? "$" + String.valueOf(0l) : "$" + transaction.getAmount().toString());
                        log.info("MonthlyAmountDto: {}", monthlyAmountDto);
                        monthlyAmountList.add(monthlyAmountDto);
                    }
                    flag++;
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new CalculateDiscountPointsException(exception.getLocalizedMessage());
        }
        return monthlyAmountList;
    }

    private Optional<Customer> validateCustomer(Long customerId) {
        if (customerId == null) {
            log.info("Customer id is null. Please provide a customer id.");
            return Optional.empty();
        }
        log.info("Calculation of discount points started for customer id: " + customerId);
        Optional<Customer> customer;
        try {
            customer = customerRepository.findByCustomerId(customerId);
            if (customer.isPresent()) {
                log.debug("Customer details: " + customer);
            } else {
                customer = Optional.empty();
            }
        } catch (Exception exception) {
            log.error("Error occurred while fetching customer details with customer id: " + customerId + " as " + exception.getLocalizedMessage());
            throw new CustomerNotFoundException();
        }
        return customer;
    }

    /***
     * Quarterly discount points calculated
     */
    private Long calculateQuarterlyDiscountPoints(List<MonthlyAmountDto> monthlyAmounts) {

        Long qurterlyAmount = 0l;
        for (MonthlyAmountDto monthlyAmountDto : monthlyAmounts) {
            qurterlyAmount = qurterlyAmount + monthlyAmountDto.getRewardPoints();
        }
        return qurterlyAmount;
    }

    private Optional<Long> calculateMonthlyDiscountPoints(Transaction transaction) {

        long monthlyDiscountPoints = 0l;
        try {
            Double amount = transaction.getAmount();

            if (amount != 0.00 && amount != null) {
                // A customer receives 2 points for every dollar spent over $100 in each transaction
                if (amount > 100.00) {
                    monthlyDiscountPoints = (long) ((Math.floor(amount - 100.00)) * 2);
                }
                // 1 point for every dollar spent between $50 and $100 in each transaction
                // $120 = 2*$20 +1*$50 = 90 pts
                if (amount >= 100.00) {
                    monthlyDiscountPoints = monthlyDiscountPoints + 50;
                }
                if (amount >= 50.00 && amount < 100.00) {
                    monthlyDiscountPoints = (long) (monthlyDiscountPoints + (Math.floor(100.00 - amount)));
                }
            }
        } catch (NullPointerException exception) {
            exception.printStackTrace();
            throw new NullPointerException(exception.getLocalizedMessage());
        } catch (ArithmeticException exception) {
            exception.printStackTrace();
            throw new MonthlyDiscountPointsArithmeticException(exception.getLocalizedMessage());
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new MonthlyDiscountPointsException(exception.getLocalizedMessage());
        }
        log.debug("Monthly discount points: {}", monthlyDiscountPoints);
        return Optional.of(monthlyDiscountPoints);
    }
}
