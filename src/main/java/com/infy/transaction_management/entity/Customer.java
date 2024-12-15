package com.infy.transaction_management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Set;

@Entity
@Table(name = "customer")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Customer {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "ID")
    private Long id;

    @NotNull(message = "Customer name is mandatory")
    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "customer_id")
    private Long customerId;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private Set<Transaction> transactions;
}
