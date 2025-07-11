package edu.uniquindio.dentalmanagementsystembackend.service.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Servicio para verificación de reCAPTCHA v3.
 * reCAPTCHA v3 analiza el comportamiento del usuario y devuelve un score
 * entre 0.0 (muy probablemente un bot) y 1.0 (muy probablemente humano).
 */
@Service
@Slf4j
public class CaptchaService {

    // Inyección del valor de la clave secreta de reCAPTCHA desde el archivo de configuración
    @Value("${recaptcha.secret-key}")
    private String recaptchaSecretKey;

    // URL de verificación de Google reCAPTCHA v3
    private static final String GOOGLE_RECAPTCHA_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    // Umbral de score por defecto para considerar válido (configurable)
    @Value("${recaptcha.score-threshold:0.5}")
    private double scoreThreshold;

    // Cliente web para realizar solicitudes HTTP
    private final WebClient webClient;

    // Constructor que inicializa el cliente web con la URL base de verificación de reCAPTCHA
    public CaptchaService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl(GOOGLE_RECAPTCHA_VERIFY_URL)
                .build();
    }

    /**
     * Verifica el token de reCAPTCHA v3 y valida el score.
     * 
     * @param captchaToken Token de reCAPTCHA enviado por el frontend
     * @return Mono<Boolean> true si el score es válido, false en caso contrario
     */
    public Mono<Boolean> verifyCaptcha(String captchaToken) {
        return verifyCaptchaWithScore(captchaToken)
                .map(score -> {
                    boolean isValid = score >= scoreThreshold;
                    log.info("reCAPTCHA score: {}, threshold: {}, valid: {}", score, scoreThreshold, isValid);
                    return isValid;
                });
    }

    /**
     * Verifica el token de reCAPTCHA v3 y devuelve el score.
     * 
     * @param captchaToken Token de reCAPTCHA enviado por el frontend
     * @return Mono<Double> Score entre 0.0 y 1.0
     */
    public Mono<Double> verifyCaptchaWithScore(String captchaToken) {
        if (captchaToken == null || captchaToken.trim().isEmpty()) {
            log.warn("Token de reCAPTCHA es nulo o vacío");
            return Mono.just(0.0);
        }

        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("secret", recaptchaSecretKey)
                        .queryParam("response", captchaToken)
                        .build())
                .retrieve()
                .bodyToMono(GoogleResponse.class)
                .map(response -> {
                    if (!response.isSuccess()) {
                        log.warn("reCAPTCHA verification failed: {}", response.getErrorCodes());
                        return 0.0;
                    }
                    return response.getScore();
                })
                .onErrorReturn(0.0) // En caso de error, considerar como bot
                .doOnError(error -> log.error("Error verificando reCAPTCHA: {}", error.getMessage()));
    }

    /**
     * Verifica el token de reCAPTCHA v3 con un umbral personalizado.
     * 
     * @param captchaToken Token de reCAPTCHA enviado por el frontend
     * @param customThreshold Umbral personalizado (0.0 a 1.0)
     * @return Mono<Boolean> true si el score es válido según el umbral personalizado
     */
    public Mono<Boolean> verifyCaptchaWithCustomThreshold(String captchaToken, double customThreshold) {
        return verifyCaptchaWithScore(captchaToken)
                .map(score -> {
                    boolean isValid = score >= customThreshold;
                    log.info("reCAPTCHA score: {}, custom threshold: {}, valid: {}", 
                            score, customThreshold, isValid);
                    return isValid;
                });
    }

    /**
     * Clase interna que representa la respuesta de Google reCAPTCHA v3.
     * Incluye el score y códigos de error para debugging.
     */
    @Data
    private static class GoogleResponse {
        private boolean success;
        private double score;
        private String action;
        private String challengeTs;
        private String hostname;
        
        @JsonProperty("error-codes")
        private List<String> errorCodes;
    }
}