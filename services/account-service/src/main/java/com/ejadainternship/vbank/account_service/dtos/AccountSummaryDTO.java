package com.ejadainternship.vbank.account_service.dtos;

public record AccountSummaryDTO(
        String accountId,
        String accountNumber,
        String message
) {
}
