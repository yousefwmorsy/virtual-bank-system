package com.example.transactionservice.controllers;

import com.example.transactionservice.dtos.TransactionHistoryResponseDTO;
import com.example.transactionservice.services.TransactionService;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/accounts")
@Data
public class AccountTransactionController {
    final private TransactionService transactionService;

    @GetMapping("/{accountId}/transactions")
    public ResponseEntity<List<TransactionHistoryResponseDTO>> getTransactionHistory(
            @PathVariable UUID accountId) {
        return ResponseEntity.ok(transactionService.getTransactionHistory(accountId));
    }
}