package com.ejadainternship.vbank.bff_service.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record AccountTransactionsDTO(
        UUID transactionId,
        UUID fromAccountId,
        UUID toAccountId,
        BigDecimal amount,
        String description,
        LocalDateTime timestamp,
        String deliveryStatus
) {
}
