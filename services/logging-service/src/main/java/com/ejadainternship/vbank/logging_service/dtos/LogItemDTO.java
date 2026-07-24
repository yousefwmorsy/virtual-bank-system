package com.ejadainternship.vbank.logging_service.dtos;

public record LogItemDTO(
        String message,
        String messageType,
        String dateTime
) {
}
