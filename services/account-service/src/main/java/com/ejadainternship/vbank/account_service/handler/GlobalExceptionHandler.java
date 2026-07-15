package com.ejadainternship.vbank.account_service.handler;

import com.ejadainternship.vbank.account_service.exceptions.AccountDoesNotExistException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AccountDoesNotExistException.class)
    public ResponseEntity<?> handleResourceNotFoundException(AccountDoesNotExistException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
}
