package com.infy.transactionManagement.serviceImpl;

import com.infy.transactionManagement.dto.CustomerDto;
import com.infy.transactionManagement.dto.TransactionDetailsDto;
import com.infy.transactionManagement.entity.Customer;
import com.infy.transactionManagement.entity.Transaction;
import com.infy.transactionManagement.repository.CustomerRepository;
import com.infy.transactionManagement.service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public Optional<CustomerDto> getCustomerDetails(Long customerId) {
        log.info("Service data" + customerId);
        Optional<Customer> customer = customerRepository.findByCustomerId(customerId);
        return Optional.empty();
    }

    @Override
    public Optional<CustomerDto> saveCustomerDetails(CustomerDto customerDto) {

        /*Customer customer = new Customer();
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
        transactions.add(transaction);*/

        Customer customer = new Customer();
        customer.setCustomerId(2l);
        customer.setCustomerName("Steve");

        Set<Transaction> transactions = new HashSet<>();
        Transaction transaction = new Transaction();
        transaction.setMonth("JAN");
        transaction.setAmount(888.67);
        transaction.setCustomer(customer);
        transactions.add(transaction);

        transaction = new Transaction();
        transaction.setMonth("FEB");
        transaction.setAmount(444.5);
        transaction.setCustomer(customer);
        transactions.add(transaction);

        customer.setTransactions(transactions);
        Customer customerDetails = customerRepository.save(customer);
        return Optional.empty();
    }

    @Override
    public Optional<TransactionDetailsDto> calculateDiscountPoints(Long customerId) {
        Optional<Customer> customer = customerRepository.findByCustomerId(customerId);

        TransactionDetailsDto transactionDetailsDto = new TransactionDetailsDto();
        Long qurterlyAmount = 0l;
        Long monthlyAmount = 0l;
        List<Long> monthlyAmounts = new ArrayList<>();

        if (customer.isPresent() && !customer.get().getTransactions().isEmpty()) {
            for (Transaction transaction : customer.get().getTransactions()) {
                monthlyAmount = calculateMonthlyDiscountPoints(transaction);
                if (monthlyAmount != null || monthlyAmount != 0l) {
                    monthlyAmounts.add(monthlyAmount);
                    qurterlyAmount = qurterlyAmount + monthlyAmount;
                }
            }
        }

        transactionDetailsDto.setMonthlyAmount(monthlyAmounts);
        transactionDetailsDto.setQurterlyAmount(qurterlyAmount);

        return Optional.of(transactionDetailsDto);
    }

    private Long calculateMonthlyDiscountPoints(Transaction transaction) {

        Double amount = transaction.getAmount();
        Double value;
        Long monthlyDiscountPoints = null;

        if (amount != 0.00 && amount != null) {
            if (amount > 100.00) {
                value = (amount - 100.00) * 2;
                monthlyDiscountPoints = Double.valueOf(value).longValue();
            }

            if (amount >= 50.00 && amount <= 100.00) {

            }
        }

        return monthlyDiscountPoints;
    }
}
