package edu.uniquindio.dentalmanagementsystembackend.exception;

/**
 * Excepción específica para errores de autenticación.
 * Se lanza cuando hay problemas con el proceso de login.
 */
public class AuthenticationException extends RuntimeException {
    
    private final String errorCode;
    
    public AuthenticationException(String message) {
        super(message);
        this.errorCode = "AUTH_ERROR";
    }
    
    public AuthenticationException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "AUTH_ERROR";
    }
    
    public String getErrorCode() {
        return errorCode;
    }
} 