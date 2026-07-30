package com.example.transactionservice.dtos;

public record ErrorResponseDTO(
        int status,
        String error,
        String message
) {
}