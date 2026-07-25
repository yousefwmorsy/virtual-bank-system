package com.example.transactionservice.entities;

import com.example.transactionservice.enums.Status;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID fromAccountId;

    @Column(nullable = false)
    private UUID toAccountId;

    @Column(nullable = false)
    private BigDecimal amount;

    private String description;

    private Status status = Status.INITIATED;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime initiatedAt;
}
