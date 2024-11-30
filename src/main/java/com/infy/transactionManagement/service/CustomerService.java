package com.infy.transactionManagement.service;

import com.infy.transactionManagement.dto.CustomerDto;
import com.infy.transactionManagement.dto.TransactionDetailsDto;

import java.util.Optional;

public interface CustomerService {

    Optional<CustomerDto> getCustomerDetails(Long customerId);

    Optional<CustomerDto> saveCustomerDetails(CustomerDto customerDto);

    Optional<TransactionDetailsDto> calculateDiscountPoints(Long customerId);
}
