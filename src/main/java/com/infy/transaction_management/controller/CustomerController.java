package com.infy.transaction_management.controller;

import com.infy.transaction_management.dto.CustomerDto;
import com.infy.transaction_management.dto.TransactionDetailsDto;
import com.infy.transaction_management.service.CustomerService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @PostMapping("/save-customers")
    public ResponseEntity<List<CustomerDto>> saveCustomerDetails(@Valid @RequestBody List<CustomerDto> customerDtos) {
        Optional<List<CustomerDto>> customerDetails = customerService.saveCustomerDetails(customerDtos);
        if (customerDetails.isPresent() && !customerDetails.get().isEmpty()) {
            log.debug("Saved Customers Details: " + customerDetails.get());
            return ResponseEntity.status(HttpStatus.CREATED).body(customerDetails.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/calculate-points/{id}")
    public ResponseEntity<TransactionDetailsDto> calculateDiscountPoints(@NotNull @PathVariable("id") Long id) {

        Optional<TransactionDetailsDto> transactionDetailsDto = customerService.calculateDiscountPoints(id);
        if (transactionDetailsDto.isPresent()) {
            log.info("Discount points for user id {} calculated successfully for Transaction: {}", id, transactionDetailsDto.get());
            return ResponseEntity.ok(transactionDetailsDto.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
