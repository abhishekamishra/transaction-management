package com.infy.transactionManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CustomerDto implements Serializable {

    private Long id;

    private String customerName;

    private Long customerId;

    private Set<TransactionDto> transactions;
}
