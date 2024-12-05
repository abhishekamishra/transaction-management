package com.infy.transaction_management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "transaction")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "customer")
@EqualsAndHashCode(exclude = "customer")
public class Transaction {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "ID")
    private Long id;

    @NotNull(message = "Month is mandatory")
    @Column(name = "month")
    private String month;

    @NotNull(message = "Amount is mandatory")
    @Column(name = "amount")
    private Double amount;

    @ManyToOne
    @JoinColumn(name = "customerId")
    private Customer customer;
}
