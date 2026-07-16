package com.ejadainternship.vbank.account_service.exceptions;

public class AccountDoesNotExistException extends RuntimeException {
    public AccountDoesNotExistException(String accountId) {
        super("Account with ID" + accountId + " not found");
    }
}
