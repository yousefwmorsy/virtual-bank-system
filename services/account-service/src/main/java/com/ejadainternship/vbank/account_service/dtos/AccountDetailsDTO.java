package com.ejadainternship.vbank.account_service.dtos;

import java.math.BigDecimal;

public record AccountDetailsDTO(
        String accountId,
        String accountNumber,
        String accountType,
        BigDecimal balance,
        String status
) {
}
