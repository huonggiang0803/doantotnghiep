package com.example.doan.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(ResponseStatusException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("email", ex.getReason());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}
