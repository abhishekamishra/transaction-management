package com.infy.transaction_management.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.LinkedHashSet;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CustomerDto implements Serializable {

    private Long id;

    @NotNull(message = "Customer name is mandatory")
    @Size(min = 2, max = 30)
    private String customerName;

    private Long customerId;

    private LinkedHashSet<TransactionDto> transactions;
}
