package com.example.transactionservice.controllers;

import com.example.transactionservice.dtos.InitiateTransactionRequest;
import com.example.transactionservice.dtos.initiateTransactionResponse;
import com.example.transactionservice.services.TransactionService;
import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/transactions")
@Data
public class TransactionController {

    final private TransactionService transactionService;

    @PostMapping("/transfer/initiation")
    public ResponseEntity<?> initiateTransaction(@Valid @RequestBody InitiateTransactionRequest initiateTransactionRequest){
        initiateTransactionResponse response = transactionService.initiateTransaction(initiateTransactionRequest);
        return ResponseEntity.ok(response);
    }



}
