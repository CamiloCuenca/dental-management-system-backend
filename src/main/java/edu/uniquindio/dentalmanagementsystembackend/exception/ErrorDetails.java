package edu.uniquindio.dentalmanagementsystembackend.exception;


public record ErrorDetails(
        int status,
        String error,
        String message
) {}
