package com.example.transactionservice.controllers;

import com.example.transactionservice.dtos.ExecutionRequest;
import com.example.transactionservice.dtos.ExecutionResponse;
import com.example.transactionservice.dtos.InitiateTransactionRequest;
import com.example.transactionservice.dtos.initiateTransactionResponse;
import com.example.transactionservice.services.TransactionService;
import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transactions")
@Data
public class TransactionController {

    final private TransactionService transactionService;

    @PostMapping("/transfer/initiation")
    public ResponseEntity<?> initiateTransaction(@Valid @RequestBody InitiateTransactionRequest initiateTransactionRequest){
        initiateTransactionResponse response = transactionService.initiateTransaction(initiateTransactionRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/transfer/execution")
    public ResponseEntity<?> executeTransaction(@Valid @RequestBody ExecutionRequest executionRequest){
        ExecutionResponse response = transactionService.executeTransaction(executionRequest);
        return ResponseEntity.ok(response);
    }





}
