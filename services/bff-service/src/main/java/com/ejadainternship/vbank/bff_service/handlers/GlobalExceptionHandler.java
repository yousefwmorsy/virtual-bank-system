package com.ejadainternship.vbank.bff_service.handlers;

import com.ejadainternship.vbank.bff_service.dtos.ErrorResponseDTO;
import com.ejadainternship.vbank.bff_service.exceptions.DownstreamServiceException;
import com.ejadainternship.vbank.bff_service.exceptions.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleUserNotFound(UserNotFoundException e) {
        log.error(e.getMessage());
        HttpStatus status = HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(
                new ErrorResponseDTO(status.value(), status.getReasonPhrase(), e.getMessage())
        );
    }

    @ExceptionHandler(DownstreamServiceException.class)
    public ResponseEntity<ErrorResponseDTO> handleDownstreamServerError(DownstreamServiceException e) {
        log.error(e.getMessage());
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
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
}