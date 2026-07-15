package com.ejadainternship.vbank.account_service.dtos;

public record CreateAccountRequestDTO(
        String userId,
        String accountType,
        String initialBalance
) {
}
