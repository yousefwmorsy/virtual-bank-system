package com.example.transactionservice.dtos;


import java.math.BigDecimal;

public record AccountDetailsDTO(
        String accountId,
        String accountNumber,
        String accountType,
        BigDecimal balance,
        String status
) {
}
