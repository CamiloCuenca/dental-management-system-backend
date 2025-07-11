package edu.uniquindio.dentalmanagementsystembackend.dto.captcha;

/**
 * DTO para la respuesta de verificación de reCAPTCHA v3.
 */
public record CaptchaResponseDTO(
    boolean success,
    boolean valid,
    String message,
    Double score,
    Double threshold
) {
    
    /**
     * Constructor para respuesta básica de verificación.
     */
    public static CaptchaResponseDTO basic(boolean valid, String message) {
        return new CaptchaResponseDTO(true, valid, message, null, null);
    }
    
    /**
     * Constructor para respuesta con score.
     */
    public static CaptchaResponseDTO withScore(double score, double threshold) {
        return new CaptchaResponseDTO(true, score >= threshold, 
                score >= threshold ? "Verificación exitosa" : "Verificación fallida", 
                score, threshold);
    }
    
    /**
     * Constructor para respuesta de error.
     */
    public static CaptchaResponseDTO error(String message) {
        return new CaptchaResponseDTO(false, false, message, null, null);
    }
} 