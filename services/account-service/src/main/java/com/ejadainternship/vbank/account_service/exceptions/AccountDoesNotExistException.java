package com.ejadainternship.vbank.account_service.exceptions;

public class AccountDoesNotExistException extends RuntimeException {
    public AccountDoesNotExistException() {
        super("Account does not exist");
    }

    public AccountDoesNotExistException(String message) {
        super(message);
    }
}
