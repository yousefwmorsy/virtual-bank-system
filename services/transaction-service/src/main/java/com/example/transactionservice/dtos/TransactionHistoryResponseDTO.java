package com.example.transactionservice.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionHistoryResponseDTO(
        UUID transactionId,
        UUID fromAccountId,
        UUID toAccountId,
        BigDecimal amount,
        String description,
        LocalDateTime timestamp,
        String deliveryStatus
) {
}
