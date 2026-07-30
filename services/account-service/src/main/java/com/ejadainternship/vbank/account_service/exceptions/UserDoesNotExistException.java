package com.ejadainternship.vbank.account_service.exceptions;

public class UserDoesNotExistException extends RuntimeException {
    public UserDoesNotExistException(String accountId) {
        super("User with ID" + accountId + " not found");
    }
}
