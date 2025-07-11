package edu.uniquindio.dentalmanagementsystembackend.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Filtro para logging de todas las peticiones HTTP
 */
@Component
@Order(1)
@Slf4j
public class LoggingFilter implements Filter {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // Capturar tiempo de inicio
        long startTime = System.currentTimeMillis();
        String timestamp = LocalDateTime.now().format(formatter);
        
        // Log de la petición entrante
        log.info("=== PETICIÓN ENTRANTE ===");
        log.info("Timestamp: {}", timestamp);
        log.info("Método: {}", httpRequest.getMethod());
        log.info("URI: {}", httpRequest.getRequestURI());
        log.info("Query String: {}", httpRequest.getQueryString() != null ? httpRequest.getQueryString() : "N/A");
        log.info("IP Cliente: {}", getClientIpAddress(httpRequest));
        log.info("User-Agent: {}", httpRequest.getHeader("User-Agent"));
        
        // Continuar con la cadena de filtros
        chain.doFilter(request, response);
        
        // Calcular tiempo de respuesta
        long duration = System.currentTimeMillis() - startTime;
        
        // Log de la respuesta
        log.info("=== RESPUESTA SALIENTE ===");
        log.info("Timestamp: {}", LocalDateTime.now().format(formatter));
        log.info("Status: {}", httpResponse.getStatus());
        log.info("Duración: {} ms", duration);
        log.info("Content-Type: {}", httpResponse.getContentType() != null ? httpResponse.getContentType() : "N/A");
        log.info("========================");
    }
    
    /**
     * Obtiene la dirección IP real del cliente
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0];
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
} 