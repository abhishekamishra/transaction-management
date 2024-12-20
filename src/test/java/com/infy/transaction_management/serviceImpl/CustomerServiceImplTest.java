package com.infy.transaction_management.serviceImpl;

import com.infy.transaction_management.dto.CustomerDto;
import com.infy.transaction_management.dto.MonthlyAmountDto;
import com.infy.transaction_management.dto.TransactionDetailsDto;
import com.infy.transaction_management.dto.TransactionDto;
import com.infy.transaction_management.entity.Customer;
import com.infy.transaction_management.entity.Transaction;
import com.infy.transaction_management.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

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
    void testCalculateDiscountPoints() throws Exception {
        // ex: $120 = 2*$20 + 1*$50 = 90pts
        // $999.56 = 2*$899 + 1*$50 = 1848pts
        // $1767.90 = 2*$1667 + 1*50 = 3384pts

        when(customerRepository.findByCustomerId(1l)).thenReturn(Optional.of(customer));
        Optional<TransactionDetailsDto> transactionDetailsDto = customerServiceImpl.calculateDiscountPoints(1l);
        List<MonthlyAmountDto> monthlyAmountDtos = transactionDetailsDto.get().getMonthlyDetails();

        assertNotNull(transactionDetailsDto.get());

        // monthly points
        assertEquals(90, monthlyAmountDtos.stream().map(cust -> cust.getRewardPoints()).findFirst().get());
        assertEquals(1848, monthlyAmountDtos.stream().map(cust -> cust.getRewardPoints()).skip(1).findFirst().get());
        assertEquals(3384, monthlyAmountDtos.stream().map(cust -> cust.getRewardPoints()).skip(2).findFirst().get());

        // quarterly points
        assertEquals(5322, transactionDetailsDto.get().getSumOfQurterlyRewardPoints());
    }

    @Test
    void testSaveCustomerDetails() throws Exception {
        List<CustomerDto> customerDtos = new ArrayList<>();
        CustomerDto customerDto = new CustomerDto();
        customerDto.setCustomerName("User_1");

        LinkedHashSet<TransactionDto> transactions = new LinkedHashSet<>();
        TransactionDto transactionDto = Mockito.mock(TransactionDto.class);
        transactions.add(transactionDto);

        customerDto.setTransactions(transactions);
        customerDtos.add(customerDto);

        customerDto = new CustomerDto();
        customerDto.setCustomerName("User_2");

        transactions = new LinkedHashSet<>();
        transactionDto = Mockito.mock(TransactionDto.class);
        transactions.add(transactionDto);

        customerDto.setTransactions(transactions);
        customerDtos.add(customerDto);

        // given
        when(customerRepository.saveAllAndFlush(any())).thenReturn(Arrays.asList(customer));

        // when
        customerServiceImpl.saveCustomerDetails(customerDtos);

        // then
        verify(customerRepository, times(1)).saveAllAndFlush(any());
    }
}