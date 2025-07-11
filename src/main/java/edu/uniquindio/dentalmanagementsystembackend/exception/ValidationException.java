package edu.uniquindio.dentalmanagementsystembackend.exception;

import java.util.Map;
import java.util.HashMap;

/**
 * Excepción específica para errores de validación de datos.
 * Incluye detalles sobre qué campos fallaron en la validación.
 */
public class ValidationException extends RuntimeException {
    
    private final Map<String, String> fieldErrors;
    private final String errorCode;
    
    public ValidationException(String message) {
        super(message);
        this.fieldErrors = new HashMap<>();
        this.errorCode = "VALIDATION_ERROR";
    }
    
    public ValidationException(String message, Map<String, String> fieldErrors) {
        super(message);
        this.fieldErrors = fieldErrors;
        this.errorCode = "VALIDATION_ERROR";
    }
    
    public ValidationException(String message, String errorCode) {
        super(message);
        this.fieldErrors = new HashMap<>();
        this.errorCode = errorCode;
    }
    
    public void addFieldError(String field, String error) {
        this.fieldErrors.put(field, error);
    }
    
    public Map<String, String> getFieldErrors() {
        return new HashMap<>(fieldErrors);
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public boolean hasFieldErrors() {
        return !fieldErrors.isEmpty();
    }
} 