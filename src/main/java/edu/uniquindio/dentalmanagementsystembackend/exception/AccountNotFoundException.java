package edu.uniquindio.dentalmanagementsystembackend.exception;

/**
 * Excepción lanzada cuando no se encuentra una cuenta específica.
 */
public class AccountNotFoundException extends BusinessException {
    
    public AccountNotFoundException(String message) {
        super(message, "ACCOUNT_NOT_FOUND");
    }
    
    public AccountNotFoundException(String message, String userMessage) {
        super(message, "ACCOUNT_NOT_FOUND", userMessage);
    }
} 