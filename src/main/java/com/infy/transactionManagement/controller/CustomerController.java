package com.infy.transactionManagement.controller;

import com.infy.transactionManagement.dto.CustomerDto;
import com.infy.transactionManagement.dto.TransactionDetailsDto;
import com.infy.transactionManagement.service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@Slf4j
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/get/{id}")
    public ResponseEntity<CustomerDto> getCustomerDetails(@PathVariable("id") Long id) {
        log.info("Controller with id" + id);

        customerService.getCustomerDetails(id);

        return ResponseEntity.ok(null);
    }

    @PostMapping("/save-customer")
    public ResponseEntity<CustomerDto> saveCustomerDetails(@RequestBody CustomerDto customerDto) {
        customerService.saveCustomerDetails(customerDto);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/calculate/{id}")
    public ResponseEntity<TransactionDetailsDto> calculateDiscountPoints(@PathVariable("id") Long id) {
        //log.info("Controller with id" + id);
        Optional<TransactionDetailsDto> transactionDetailsDto = customerService.calculateDiscountPoints(id);
        return ResponseEntity.ok(transactionDetailsDto.get());
    }
}
