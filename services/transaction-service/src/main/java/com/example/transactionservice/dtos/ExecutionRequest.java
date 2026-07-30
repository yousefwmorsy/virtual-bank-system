package com.example.transactionservice.dtos;

import lombok.Data;

import java.util.UUID;

@Data
public class ExecutionRequest {
    private UUID transactionId;
}
