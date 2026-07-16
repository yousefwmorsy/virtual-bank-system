package com.ejadainternship.vbank.account_service.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record TransferRequestDTO(
        @NotBlank String fromAccountId,
        @NotBlank String toAccountId,
        @PositiveOrZero BigDecimal amount
) {}
