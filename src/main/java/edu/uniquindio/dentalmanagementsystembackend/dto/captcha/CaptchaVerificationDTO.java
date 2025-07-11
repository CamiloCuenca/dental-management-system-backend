package edu.uniquindio.dentalmanagementsystembackend.dto.captcha;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para la verificación de reCAPTCHA v3.
 */
public record CaptchaVerificationDTO(
    @NotBlank(message = "El token de reCAPTCHA es requerido")
    String token
) {} 