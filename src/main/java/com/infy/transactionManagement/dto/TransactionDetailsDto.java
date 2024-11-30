package com.infy.transactionManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TransactionDetailsDto implements Serializable {

    private Long id;

    private List<Long> monthlyAmount;

    private Long qurterlyAmount;
}
