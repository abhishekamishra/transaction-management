package com.infy.transaction_management.util;

import com.infy.transaction_management.dto.CustomerDto;
import com.infy.transaction_management.dto.MonthlyAmountDto;
import com.infy.transaction_management.dto.TransactionDto;
import com.infy.transaction_management.entity.Customer;
import com.infy.transaction_management.entity.Transaction;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class CustomerUtil implements Serializable {

    public static double generateRandomNumber() {
        double max = Math.pow(10, 3) - 1;
        double min = Math.pow(10, 0);
        double range = max - min + 1;
        return (int) (Math.random() * range) + min;
    }

    public static List<Customer> createCustomer(List<CustomerDto> customerDtos) {
        List<Customer> customers = new ArrayList<>();
        for (CustomerDto customerDto : customerDtos) {
            Customer customer = new Customer();
            // create random numbers for customer id
            customer.setCustomerId(Double.valueOf(CustomerUtil.generateRandomNumber()).longValue());
            customer.setCustomerName(customerDto.getCustomerName() == null ? null : customerDto.getCustomerName());
            customer.setTransactions(createTransaction(customerDto.getTransactions(), customer));
            customers.add(customer);
        }
        return customers;
    }

    private static LinkedHashSet<Transaction> createTransaction(Set<TransactionDto> transactionDtos, Customer customer) {
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

    public static LinkedHashSet<TransactionDto> getTransactionsDto(Set<Transaction> transactions) {
        return transactions.stream().map(t -> new TransactionDto(t.getId() == null ? null : t.getId(), t.getMonth() == null ? null : t.getMonth(), t.getAmount() == null ? null : t.getAmount(), t.getCustomer() == null ? null : t.getCustomer().getCustomerId())).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public static MonthlyAmountDto createMonthlyAmountDto(Optional<Long> monthlyRewardPoints, Transaction transaction) {

        MonthlyAmountDto monthlyAmountDto = new MonthlyAmountDto();
        monthlyAmountDto.setMonth(transaction.getMonth());
        monthlyAmountDto.setRewardPoints(monthlyRewardPoints.isPresent() ? monthlyRewardPoints.get() : null);
        monthlyAmountDto.setAmount(transaction.getAmount() == null ? null : "$" + transaction.getAmount());

        return monthlyAmountDto;
    }
}
