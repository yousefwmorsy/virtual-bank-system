package com.example.transactionservice.dtos;

public record LogItemDTO(
        String message,
        String messageType,
        String dateTime
) {
}
