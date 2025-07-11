package edu.uniquindio.dentalmanagementsystembackend.controller;

import edu.uniquindio.dentalmanagementsystembackend.dto.captcha.CaptchaResponseDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.captcha.CaptchaVerificationDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.captcha.CaptchaVerificationWithThresholdDTO;
import edu.uniquindio.dentalmanagementsystembackend.service.impl.CaptchaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador para manejar la verificación de reCAPTCHA v3.
 * Este controlador proporciona endpoints para verificar tokens de reCAPTCHA
 * y obtener información sobre la configuración.
 */
@RestController
@RequestMapping("/api/captcha")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class CaptchaController {

    private final CaptchaService captchaService;

    /**
     * Verifica un token de reCAPTCHA v3.
     * 
     * @param request DTO con el token de reCAPTCHA
     * @return ResponseEntity con el resultado de la verificación
     */
    @PostMapping("/verify")
    public ResponseEntity<CaptchaResponseDTO> verifyCaptcha(@Valid @RequestBody CaptchaVerificationDTO request) {
        try {
            Boolean isValid = captchaService.verifyCaptcha(request.token()).block();
            return ResponseEntity.ok(CaptchaResponseDTO.basic(isValid, 
                    isValid ? "Verificación exitosa" : "Verificación fallida"));
        } catch (Exception e) {
            log.error("Error verificando reCAPTCHA: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(CaptchaResponseDTO.error("Error interno del servidor"));
        }
    }

    /**
     * Verifica un token de reCAPTCHA v3 y devuelve el score.
     * 
     * @param request DTO con el token de reCAPTCHA
     * @return ResponseEntity con el score de reCAPTCHA
     */
    @PostMapping("/verify-score")
    public ResponseEntity<CaptchaResponseDTO> verifyCaptchaWithScore(@Valid @RequestBody CaptchaVerificationDTO request) {
        try {
            Double score = captchaService.verifyCaptchaWithScore(request.token()).block();
            return ResponseEntity.ok(CaptchaResponseDTO.withScore(score, 0.5));
        } catch (Exception e) {
            log.error("Error verificando reCAPTCHA score: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(CaptchaResponseDTO.error("Error interno del servidor"));
        }
    }

    /**
     * Verifica un token de reCAPTCHA v3 con umbral personalizado.
     * 
     * @param request DTO con el token y umbral personalizado
     * @return ResponseEntity con el resultado de la verificación
     */
    @PostMapping("/verify-custom")
    public ResponseEntity<CaptchaResponseDTO> verifyCaptchaWithCustomThreshold(@Valid @RequestBody CaptchaVerificationWithThresholdDTO request) {
        try {
            Boolean isValid = captchaService.verifyCaptchaWithCustomThreshold(request.token(), request.threshold()).block();
            return ResponseEntity.ok(CaptchaResponseDTO.withScore(
                    captchaService.verifyCaptchaWithScore(request.token()).block(), 
                    request.threshold()));
        } catch (Exception e) {
            log.error("Error verificando reCAPTCHA con umbral personalizado: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(CaptchaResponseDTO.error("Error interno del servidor"));
        }
    }
} 