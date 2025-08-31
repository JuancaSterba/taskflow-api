package com.juancasterba.taskflow_api.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleResourceNotFound(ResourceNotFoundException e){
        return createErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("errors", errors);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String userFriendlyMessage = "Error de datos: Es posible que el nombre de usuario o el email ya existan.";

        // Lógica opcional para un mensaje más específico
        // Esto depende del nombre de la restricción en tu base de datos
        String rootErrorMessage = ex.getMostSpecificCause().getMessage();
        if (rootErrorMessage.contains("users_username_key") || rootErrorMessage.contains("uk_username")) { // El nombre puede variar
            userFriendlyMessage = "El nombre de usuario ya está en uso.";
        } else if (rootErrorMessage.contains("users_email_key") || rootErrorMessage.contains("uk_email")) { // El nombre puede variar
            userFriendlyMessage = "La dirección de email ya está registrada.";
        }

        return createErrorResponse(HttpStatus.CONFLICT, userFriendlyMessage); // <-- Usamos 409 CONFLICT
    }

    private ResponseEntity<Map<String, String>> createErrorResponse(HttpStatus status, String errorMessage) {
        return ResponseEntity.status(status).body(Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "error", errorMessage
        ));
    }

}
