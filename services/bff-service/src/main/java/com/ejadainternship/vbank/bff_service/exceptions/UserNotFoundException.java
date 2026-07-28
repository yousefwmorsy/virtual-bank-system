package com.ejadainternship.vbank.bff_service.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String id) {
        super("User with id: " + id + " not found.");
    }
}
