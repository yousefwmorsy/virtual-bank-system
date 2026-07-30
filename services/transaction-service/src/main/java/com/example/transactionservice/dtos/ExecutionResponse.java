package com.example.transactionservice.dtos;

import com.example.transactionservice.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class ExecutionResponse {

    private UUID transactionId;

    private Status status;

    private LocalDateTime initiatedAt;
}
