package edu.uniquindio.dentalmanagementsystembackend.exception;

/**
 * Excepción base para errores de negocio.
 * Todas las excepciones específicas del dominio deberían extender de esta.
 */
public class BusinessException extends RuntimeException {
    
    private final String errorCode;
    private final String userMessage;
    
    public BusinessException(String message) {
        super(message);
        this.errorCode = "BUSINESS_ERROR";
        this.userMessage = message;
    }
    
    public BusinessException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.userMessage = message;
    }
    
    public BusinessException(String message, String errorCode, String userMessage) {
        super(message);
        this.errorCode = errorCode;
        this.userMessage = userMessage;
    }
    
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "BUSINESS_ERROR";
        this.userMessage = message;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public String getUserMessage() {
        return userMessage;
    }
} 