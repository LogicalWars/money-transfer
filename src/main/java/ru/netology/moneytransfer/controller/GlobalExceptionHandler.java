package ru.netology.moneytransfer.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.netology.moneytransfer.dto.error.ErrorResponse;
import ru.netology.moneytransfer.exception.ApiException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException (MethodArgumentNotValidException ex) {
        String errorMessage = ex.getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("Invalid request data");
        return ResponseEntity.badRequest().body(new ErrorResponse(errorMessage, 4001));
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException (ApiException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage(), ex.getId()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleRootException (Exception ex) {
        return ResponseEntity.internalServerError().body(new ErrorResponse(ex.getMessage(), 5000));
    }
}



