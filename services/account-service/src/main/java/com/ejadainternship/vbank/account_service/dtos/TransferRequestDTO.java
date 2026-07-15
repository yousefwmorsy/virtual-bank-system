package com.ejadainternship.vbank.account_service.dtos;

import java.math.BigDecimal;

public record TransferRequestDTO(
        String fromAccountId,
        String toAccountId,
        BigDecimal amount
) {}
