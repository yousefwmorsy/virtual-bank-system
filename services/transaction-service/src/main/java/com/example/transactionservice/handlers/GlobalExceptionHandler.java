package com.example.transactionservice.handlers;


import com.example.transactionservice.dtos.ErrorResponseDTO;
import com.example.transactionservice.exception.AccountDoesNotExistException;
import com.example.transactionservice.exception.InsufficientBalanceException;
import com.example.transactionservice.exception.TransactionAlreadyCompletedException;
import com.example.transactionservice.exception.TransactionDoesNotExistException;
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

    @ExceptionHandler(TransactionDoesNotExistException.class)
    public ResponseEntity<ErrorResponseDTO> handleTransactionDoesNotExistException(TransactionDoesNotExistException e) {
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        var error = e.getBindingResult().getAllErrors().get(0);
        String fieldName = ((FieldError) error).getField();
        String message = error.getDefaultMessage();
        return ResponseEntity.status(status).body(
                new ErrorResponseDTO(status.value(), status.getReasonPhrase(), fieldName + " " + message)
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

    @ExceptionHandler(TransactionAlreadyCompletedException.class)
    public ResponseEntity<ErrorResponseDTO> handleTransactionAlreadyCompletedException(TransactionAlreadyCompletedException e) {
        HttpStatus status = HttpStatus.CONFLICT;
        return ResponseEntity.status(status).body(
                new ErrorResponseDTO(status.value(), status.getReasonPhrase(), e.getMessage())
        );
    }
}
