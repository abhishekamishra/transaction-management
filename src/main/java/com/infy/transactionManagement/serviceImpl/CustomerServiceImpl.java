package com.infy.transactionManagement.serviceImpl;

import com.infy.transactionManagement.exception.CalculateDiscountPointsException;
import com.infy.transactionManagement.exception.MonthlyDiscountPointsArithmeticException;
import com.infy.transactionManagement.exception.CustomerNotFoundException;
import com.infy.transactionManagement.dto.CustomerDto;
import com.infy.transactionManagement.dto.TransactionDetailsDto;
import com.infy.transactionManagement.dto.TransactionDto;
import com.infy.transactionManagement.entity.Customer;
import com.infy.transactionManagement.entity.Transaction;
import com.infy.transactionManagement.exception.MonthlyDiscountPointsException;
import com.infy.transactionManagement.repository.CustomerRepository;
import com.infy.transactionManagement.service.CustomerService;
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
    public Optional<CustomerDto> getCustomerDetails(Long customerId) {
        try {
            Optional<Customer> customer = customerRepository.findByCustomerId(customerId);
            if (customer.isPresent()) {
                log.debug("Customer details: " + customer);
                return getCustomerDto(customer.get());
            }
        } catch (Exception exception) {
            log.error("Error occurred while fetching customer details with customer id: " + customerId + " as " + exception.getLocalizedMessage());
            throw new CustomerNotFoundException(exception.getLocalizedMessage());
        }

        return Optional.empty();
    }

    private Optional<CustomerDto> getCustomerDto(Customer customer) {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setId(customer.getId() == null ? null : customer.getId());
        customerDto.setCustomerName(customer.getCustomerName() == null ? null : customer.getCustomerName());
        customerDto.setCustomerId(customer.getCustomerId() == null ? null : customer.getCustomerId());
        customerDto.setTransactions(customer.getTransactions() == null ? null : getTransactionsDto(customer.getTransactions()));

        return Optional.of(customerDto);
    }

    @Override
    public Optional<List<CustomerDto>> saveCustomerDetails(CustomerDto customerDto) {

        List<Customer> customers = new ArrayList<>();
        Customer customer = new Customer();
        customer.setCustomerId(1l);
        customer.setCustomerName("Ron");

        Set<Transaction> transactions = new HashSet<>();
        Transaction transaction = new Transaction();
        transaction.setMonth("JAN");
        transaction.setAmount(2000.13);
        transaction.setCustomer(customer);
        transactions.add(transaction);

        transaction = new Transaction();
        transaction.setMonth("FEB");
        transaction.setAmount(769.88);
        transaction.setCustomer(customer);
        transactions.add(transaction);
        customer.setTransactions(transactions);
        customers.add(customer);

        customer = new Customer();
        customer.setCustomerId(2l);
        customer.setCustomerName("Steve");

        transactions = new HashSet<>();
        transaction = new Transaction();
        transaction.setMonth("JAN");
        transaction.setAmount(888.67);
        transaction.setCustomer(customer);
        transactions.add(transaction);

        transaction = new Transaction();
        transaction.setMonth("FEB");
        transaction.setAmount(120.00);
        transaction.setCustomer(customer);
        transactions.add(transaction);
        customer.setTransactions(transactions);
        customers.add(customer);

        List<Customer> customerDetails = customerRepository.saveAllAndFlush(customers);
        List<CustomerDto> customerDtos = customerDetails.stream().map(cust -> new CustomerDto(cust.getId(), cust.getCustomerName(), cust.getCustomerId(), getTransactionsDto(cust.getTransactions()))).collect(Collectors.toList());
        return Optional.of(customerDtos);
    }

    private Set<TransactionDto> getTransactionsDto(Set<Transaction> transactions) {
        return transactions.stream().map(t -> new TransactionDto(t.getId(), t.getMonth(), t.getAmount(), t.getCustomer().getCustomerId())).collect(Collectors.toSet());
    }

    @Override
    public Optional<TransactionDetailsDto> calculateDiscountPoints(Long customerId) {
        log.info("Calculation of discount points started for customer id: " + customerId);
        Optional<Customer> customer = Optional.empty();
        try {
            customer = customerRepository.findByCustomerId(customerId);
            if (customer.isPresent()) {
                log.debug("Customer details: " + customer);
            }
        } catch (Exception exception) {
            log.error("Error occurred while fetching customer details with customer id: " + customerId + " as " + exception.getLocalizedMessage());
            throw new CustomerNotFoundException();
        }

        TransactionDetailsDto transactionDetailsDto = new TransactionDetailsDto();
        Long qurterlyAmount = 0l;
        Long monthlyAmount = 0l;
        List<Long> monthlyAmounts = new ArrayList<>();

        try {
            if (customer.isPresent() && !customer.get().getTransactions().isEmpty()) {
                for (Transaction transaction : customer.get().getTransactions()) {
                    monthlyAmount = calculateMonthlyDiscountPoints(transaction).isPresent() ? calculateMonthlyDiscountPoints(transaction).get() : 0l;
                    if (monthlyAmount != null || monthlyAmount != 0l) {
                        monthlyAmounts.add(monthlyAmount);
                        qurterlyAmount = qurterlyAmount + monthlyAmount;
                    }
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new CalculateDiscountPointsException(exception.getLocalizedMessage());
        }

        transactionDetailsDto.setCustomerName(customer.get().getCustomerName());
        transactionDetailsDto.setMonthlyAmount(monthlyAmounts);
        transactionDetailsDto.setQurterlyAmount(qurterlyAmount);

        return Optional.of(transactionDetailsDto);
    }

    private Optional<Long> calculateMonthlyDiscountPoints(Transaction transaction) {

        Double value;
        Long monthlyDiscountPoints = 0l;

        try {
            Double amount = transaction.getAmount();

            if (amount != 0.00 && amount != null) {

                // A customer receives 2 points for every dollar spent over $100 in each transaction
                if (amount > 100.00) {
                    value = (amount - 100.00) * 2;
                    monthlyDiscountPoints = Double.valueOf(value).longValue();
                }

                // 1 point for every dollar spent between $50 and $100 in each transaction
                // $120 = 2*$20 +1*$50 = 90 pts
                if (amount >= 100.00) {
                    monthlyDiscountPoints = monthlyDiscountPoints + (1 * 50);
                } else if (amount >= 50.00 && amount < 100.00) {
                    monthlyDiscountPoints = monthlyDiscountPoints + (1 * Double.valueOf(100.00 - amount).longValue());
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

        return Optional.of(monthlyDiscountPoints);
    }
}
