package com.infy.transactionManagement.serviceImpl;

import com.infy.transactionManagement.dto.TransactionDetailsDto;
import com.infy.transactionManagement.entity.Customer;
import com.infy.transactionManagement.entity.Transaction;
import com.infy.transactionManagement.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerServiceImpl customerServiceImpl;

    private Customer customer;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        Set<Transaction> transactions = new LinkedHashSet<>();

        customer = new Customer();
        customer.setId(1l);
        customer.setCustomerId(1l);
        customer.setCustomerName("Venky");

        Transaction transaction = new Transaction();
        transaction.setId(1l);
        transaction.setMonth("JAN");
        transaction.setAmount(120.00);
        transaction.setCustomer(customer);
        transactions.add(transaction);

        transaction = new Transaction();
        transaction.setId(2l);
        transaction.setMonth("FEB");
        transaction.setAmount(999.56);
        transaction.setCustomer(customer);
        transactions.add(transaction);

        transaction = new Transaction();
        transaction.setId(3l);
        transaction.setMonth("MAR");
        transaction.setAmount(1767.90);
        transaction.setCustomer(customer);
        transactions.add(transaction);

        customer.setTransactions(transactions);
    }

    @Test
    void calculateDiscountPoints() {

        // ex: $120 = 2*$20 + 1*$50 = 90pts
        // $999.56 = 2*$899 + 1*$50 = 1848pts
        // $1767.90 = 2*$1667 + 1*50 = 3384pts

        when(customerRepository.findByCustomerId(1l)).thenReturn(Optional.of(customer));
        Optional<TransactionDetailsDto> transactionDetailsDto = customerServiceImpl.calculateDiscountPoints(1l);
        Map<String, Long> map = transactionDetailsDto.get().getMonthlyAmount().get(0);

        assertNotNull(transactionDetailsDto.get());

        // monthly points
        assertEquals(90, map.entrySet().stream().map(cust -> cust.getValue()).findFirst().get());
        assertEquals(1848, map.entrySet().stream().map(cust -> cust.getValue()).skip(1).findFirst().get());
        assertEquals(3384, map.entrySet().stream().map(cust -> cust.getValue()).skip(2).findFirst().get());

        // quarterly points
        assertEquals(5322, transactionDetailsDto.get().getQurterlyAmount().entrySet().stream().map(e -> e.getValue()).findFirst().get());
    }
}