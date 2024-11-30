package com.infy.transactionManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TransactionDto implements Serializable {

    private Long id;

    private String month;

    private Double amount;

    private Long customerId;
}
