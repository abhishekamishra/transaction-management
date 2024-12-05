package com.infy.transaction_management.dto;

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

    private Long customerId;
    private String customerName;
    private List<MonthlyAmountDto> monthlyDetails;
    private Long qurterlyRewardPoints;
}
