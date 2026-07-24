package com.ejadainternship.vbank.account_service.dtos;

public record LogItemDTO(
        String message,
        String messageType,
        String dateTime
) {
}
