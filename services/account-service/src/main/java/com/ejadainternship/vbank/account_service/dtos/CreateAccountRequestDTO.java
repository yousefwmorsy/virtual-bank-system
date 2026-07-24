package com.ejadainternship.vbank.account_service.dtos;

import com.ejadainternship.vbank.account_service.models.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record CreateAccountRequestDTO(
        @NotBlank String userId,
        AccountType accountType,
        @PositiveOrZero BigDecimal initialBalance
        ) {
}
