package edu.uniquindio.dentalmanagementsystembackend.dto.captcha;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para la verificación de reCAPTCHA v3 con umbral personalizado.
 */
public record CaptchaVerificationWithThresholdDTO(
    @NotBlank(message = "El token de reCAPTCHA es requerido")
    String token,
    
    @NotNull(message = "El umbral es requerido")
    @DecimalMin(value = "0.0", message = "El umbral debe ser mayor o igual a 0.0")
    @DecimalMax(value = "1.0", message = "El umbral debe ser menor o igual a 1.0")
    Double threshold
) {} 