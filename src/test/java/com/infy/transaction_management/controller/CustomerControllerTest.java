package com.infy.transaction_management.controller;

import com.infy.transaction_management.dto.CustomerDto;
import com.infy.transaction_management.dto.TransactionDetailsDto;
import com.infy.transaction_management.dto.TransactionDto;
import com.infy.transaction_management.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;

public class CustomerControllerTest {

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController customerController;

    private List<CustomerDto> customerDtos;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        customerDtos = new ArrayList<>();
        CustomerDto customerDto = new CustomerDto();
        customerDto.setCustomerName("User_1");

        LinkedHashSet<TransactionDto> transactions = new LinkedHashSet<>();
        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setCustomerId(123l);
        transactionDto.setMonth("JAN");
        transactionDto.setAmount(123.00);
        transactions.add(transactionDto);

        customerDto.setTransactions(transactions);
        customerDtos.add(customerDto);

        customerDto = new CustomerDto();
        customerDto.setCustomerName("User_2");

        transactions = new LinkedHashSet<>();
        transactionDto = new TransactionDto();
        transactionDto.setCustomerId(76l);
        transactionDto.setMonth("FEB");
        transactionDto.setAmount(1237.00);
        transactions.add(transactionDto);

        customerDto.setTransactions(transactions);
        customerDtos.add(customerDto);

        this.mockMvc = MockMvcBuilders.standaloneSetup(customerController).build();
    }

    @Test
    void test_calculateDiscountPoints_success() throws Exception {

        long customerId = 76l;

        TransactionDetailsDto transactionDetailsDto = new TransactionDetailsDto();
        transactionDetailsDto.setCustomerId(customerId);
        transactionDetailsDto.setSumOfQurterlyRewardPoints(2324l);

        when(customerService.calculateDiscountPoints(customerId)).thenReturn(Optional.of(transactionDetailsDto));
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/customer/calculate-points/76").accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals("Success", 200, result.getResponse().getStatus());
    }

    @Test
    void test_calculateDiscountPoints_failure() throws Exception {

        long customerId = 999l;

        TransactionDetailsDto transactionDetailsDto = new TransactionDetailsDto();
        transactionDetailsDto.setCustomerId(customerId);
        transactionDetailsDto.setCustomerName("test_user");

        when(customerService.calculateDiscountPoints(customerId)).thenReturn(Optional.of(transactionDetailsDto));
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/customer/calculate-points/11").accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals("Failure", 404, result.getResponse().getStatus());
    }

    @Test
    void test_saveCustomerDetails_success() throws Exception {

        when(customerService.saveCustomerDetails(anyList())).thenReturn(Optional.of(customerDtos));
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/customer/save-customers").accept(MediaType.APPLICATION_JSON).content(anyList().toString()).contentType(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();

        assertEquals("Success", HttpStatus.CREATED.value(), response.getStatus());
    }

    @Test
    void test_saveCustomerDetails_failure() throws Exception {

        when(customerService.saveCustomerDetails(anyList())).thenReturn(Optional.empty());
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/customer/save-customers").accept(MediaType.APPLICATION_JSON).content(anyList().toString()).contentType(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();

        assertEquals("Failure", HttpStatus.NOT_FOUND.value(), response.getStatus());
    }
}
