package com.infy.transaction_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MonthlyAmountDto implements Serializable {
    private String month;
    private String amount;
    private Long rewardPoints;
}
