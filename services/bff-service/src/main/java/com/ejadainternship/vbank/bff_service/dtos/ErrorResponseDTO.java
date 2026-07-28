package com.ejadainternship.vbank.bff_service.dtos;

public record ErrorResponseDTO(
        int status,
        String error,
        String message
) {
}
