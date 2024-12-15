package com.infy.transaction_management.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @NotNull(message = "Month is mandatory")
    @Size(min = 3, max = 3)
    private String month;

    @NotNull
    @DecimalMax("100000.0")
    @DecimalMin("0.0")
    private Double amount;

    private Long customerId;
}
