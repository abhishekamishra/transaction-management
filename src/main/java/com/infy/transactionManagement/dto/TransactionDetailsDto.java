package com.infy.transactionManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TransactionDetailsDto implements Serializable {

    private String customerName;

    private List<Map<String,Long>> monthlyAmount;

    private Map<String, Long> qurterlyAmount;
}
