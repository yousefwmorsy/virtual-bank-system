package com.example.transactionservice.exception;

public class TransactionAlreadyCompletedException extends RuntimeException {
    public TransactionAlreadyCompletedException(String message) {
        super(message);
    }
}
