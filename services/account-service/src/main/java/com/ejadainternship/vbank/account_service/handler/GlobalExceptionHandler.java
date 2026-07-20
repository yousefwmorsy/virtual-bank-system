package com.ejadainternship.vbank.account_service.handler;

import com.ejadainternship.vbank.account_service.dtos.ErrorResponseDTO;
import com.ejadainternship.vbank.account_service.exceptions.AccountDoesNotExistException;
import com.ejadainternship.vbank.account_service.exceptions.InsufficientBalanceException;
import com.ejadainternship.vbank.account_service.exceptions.UserDoesNotExistException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AccountDoesNotExistException.class)
    public ResponseEntity<ErrorResponseDTO> handleAccountDoesNotExistException(AccountDoesNotExistException e) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(
                new ErrorResponseDTO(status.value(), status.getReasonPhrase(), e.getMessage())
        );
    }

    @ExceptionHandler(UserDoesNotExistException.class)
    public ResponseEntity<ErrorResponseDTO> handleUserDoesNotExistException(UserDoesNotExistException e) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(
                new ErrorResponseDTO(status.value(), status.getReasonPhrase(), e.getMessage())
        );
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ErrorResponseDTO> handleInsufficientBalanceException(InsufficientBalanceException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(
                new ErrorResponseDTO(status.value(), status.getReasonPhrase(), e.getMessage())
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleException(Exception e) {
        log.error(e.getMessage());
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(status).body(
                new ErrorResponseDTO(status.value(), status.getReasonPhrase(), "An unexpected error occurred.")
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        var error = e.getBindingResult().getAllErrors().getFirst();
        String fieldName = ((FieldError) error).getField();
        String message = error.getDefaultMessage();

        return ResponseEntity.status(status).body(
                new ErrorResponseDTO(status.value(), status.getReasonPhrase(), fieldName + " " + message)
        );
    }
}
