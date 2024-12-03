package com.infy.transactionManagement.controller;

import com.infy.transactionManagement.dto.CustomerDto;
import com.infy.transactionManagement.dto.TransactionDetailsDto;
import com.infy.transactionManagement.service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping("/get-details/{id}")
    public ResponseEntity<CustomerDto> getCustomerDetails(@PathVariable("id") Long id) {

        Optional<CustomerDto> customerDto = customerService.getCustomerDetails(id);
        if (customerDto.isPresent()) {
            log.debug("Customer Details: " + customerDto.get());
            return ResponseEntity.ok(customerDto.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/save-customer")
    public ResponseEntity<List<CustomerDto>> saveCustomerDetails(@RequestBody List<CustomerDto> customerDtos) {
        Optional<List<CustomerDto>> customerDetails = customerService.saveCustomerDetails(customerDtos);
        if (customerDetails.isPresent()) {
            log.debug("Saved Customers Details: " + customerDetails.get());
            return ResponseEntity.ok(customerDetails.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/calculate-points/{id}")
    public ResponseEntity<TransactionDetailsDto> calculateDiscountPoints(@PathVariable("id") Long id) {

        Optional<TransactionDetailsDto> transactionDetailsDto = customerService.calculateDiscountPoints(id);
        if (transactionDetailsDto.isPresent()) {
            log.debug("Transaction points details: " + transactionDetailsDto.get());
            return ResponseEntity.ok(transactionDetailsDto.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
