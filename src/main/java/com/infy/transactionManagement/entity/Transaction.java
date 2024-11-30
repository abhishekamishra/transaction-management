package com.infy.transactionManagement.entity;

import jakarta.persistence.*;
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

    @Column(name = "month")
    private String month;

    @Column(name = "amount")
    private Double amount;

    @ManyToOne
    @JoinColumn(name = "customerId")
    private Customer customer;
}
