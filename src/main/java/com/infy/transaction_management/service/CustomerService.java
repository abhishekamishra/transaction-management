package com.infy.transaction_management.service;

import com.infy.transaction_management.dto.CustomerDto;
import com.infy.transaction_management.dto.TransactionDetailsDto;

import java.util.List;
import java.util.Optional;

public interface CustomerService {

    Optional<List<CustomerDto>> saveCustomerDetails(List<CustomerDto> customerDtos);

    Optional<TransactionDetailsDto> calculateDiscountPoints(Long customerId);
}
