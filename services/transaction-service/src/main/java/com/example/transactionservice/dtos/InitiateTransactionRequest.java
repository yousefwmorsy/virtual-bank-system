package com.example.transactionservice.dtos;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;


import java.math.BigDecimal;
import java.util.UUID;

@Data
public class InitiateTransactionRequest {
    @NotNull(message = "From Account ID is required")
    private UUID fromAccountId;

    @NotNull(message = "To Account ID is required")
    private UUID toAccountId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Transfer amount must be greater than zero")
    private BigDecimal amount;

    private String description;
}
