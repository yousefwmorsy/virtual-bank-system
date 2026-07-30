package com.example.transactionservice.exception;

public class TransactionDoesNotExistException extends RuntimeException {
    public TransactionDoesNotExistException(String message) {
        super(message);
    }
}
