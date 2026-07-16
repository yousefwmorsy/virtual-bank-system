package com.ejadainternship.vbank.account_service.dtos;

import java.math.BigDecimal;

public record CreateAccountRequestDTO(
        String userId,
        String accountType,
        BigDecimal initialBalance
) {
}
