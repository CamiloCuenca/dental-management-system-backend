package edu.uniquindio.dentalmanagementsystembackend.service.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

// Anotación que indica que esta clase es un servicio de Spring
@Service
public class CaptchaService {

    // Inyección del valor de la clave secreta de reCAPTCHA desde el archivo de configuración
    @Value("${recaptcha.secret-key}")
    private String recaptchaSecretKey;

    // URL de verificación de Google reCAPTCHA
    private static final String GOOGLE_RECAPTCHA_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    // Cliente web para realizar solicitudes HTTP
    private final WebClient webClient;

    // Constructor que inicializa el cliente web con la URL base de verificación de reCAPTCHA
    public CaptchaService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl(GOOGLE_RECAPTCHA_VERIFY_URL)
                .build();
    }

    // Método para verificar el token de reCAPTCHA
    public Mono<Boolean> verifyCaptcha(String captchaToken) {
        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("secret", recaptchaSecretKey)
                        .queryParam("response", captchaToken)
                        .build())
                .retrieve()
                .bodyToMono(GoogleResponse.class)
                .map(GoogleResponse::isSuccess)
                .onErrorReturn(false); // Manejo de errores: si falla, se devuelve false
    }

    // Clase interna que representa la respuesta de Google reCAPTCHA
    @Data
    private static class GoogleResponse {
        private boolean success;

        @JsonProperty("challenge_ts")
        private String challengeTs;

        private String hostname;
        private float score;
    }
}