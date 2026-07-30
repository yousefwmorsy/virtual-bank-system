package com.example.userservice.dtos;

public record LogItemDTO(
        String message,
        String messageType,
        String dateTime
) {
}
