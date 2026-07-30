package com.ejadainternship.vbank.account_service.dtos;

public record ErrorResponseDTO(
        int status,
        String error,
        String message
) {
}
