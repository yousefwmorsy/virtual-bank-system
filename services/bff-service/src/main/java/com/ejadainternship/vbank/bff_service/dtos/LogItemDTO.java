package com.ejadainternship.vbank.bff_service.dtos;

public record LogItemDTO(
        String message,
        String messageType,
        String dateTime
) {
}
