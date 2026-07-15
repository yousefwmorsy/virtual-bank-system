package com.ejadainternship.vbank.account_service.exceptions;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException() {
        super("Balance is insufficient");
    }
}
