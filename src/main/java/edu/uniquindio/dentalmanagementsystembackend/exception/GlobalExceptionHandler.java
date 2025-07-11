package edu.uniquindio.dentalmanagementsystembackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones para toda la aplicación.
 * Proporciona respuestas consistentes para diferentes tipos de errores.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja excepciones de validación de datos.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ErrorResponse errorResponse = new ErrorResponse(
                "VALIDATION_ERROR",
                "Error de validación en los datos de entrada",
                "Los datos proporcionados no son válidos",
                LocalDateTime.now(),
                errors
        );

        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * Maneja excepciones de negocio específicas.
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getErrorCode(),
                ex.getMessage(),
                ex.getUserMessage(),
                LocalDateTime.now(),
                null
        );

        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * Maneja excepciones de autenticación.
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getErrorCode(),
                ex.getMessage(),
                "Error de autenticación",
                LocalDateTime.now(),
                null
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    /**
     * Maneja excepciones de validación específicas.
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getErrorCode(),
                ex.getMessage(),
                "Error de validación",
                LocalDateTime.now(),
                ex.getFieldErrors()
        );

        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * Maneja excepciones de recursos no encontrados.
     */
    @ExceptionHandler({UserNotFoundException.class, AccountNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleNotFoundException(RuntimeException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                "NOT_FOUND",
                ex.getMessage(),
                "El recurso solicitado no fue encontrado",
                LocalDateTime.now(),
                null
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * Maneja excepciones específicas de citas.
     */
    @ExceptionHandler(CitaException.class)
    public ResponseEntity<ErrorResponse> handleCitaException(CitaException ex) {
        String userMessage = switch (ex.getErrorType()) {
            case CITA_NO_ENCONTRADA -> "La cita solicitada no fue encontrada";
            case CITA_YA_EXISTE -> "Ya existe una cita en ese horario";
            case DOCTOR_NO_DISPONIBLE -> "El doctor no está disponible en ese horario";
            case HORARIO_NO_DISPONIBLE -> "El horario seleccionado no está disponible";
            case PACIENTE_NO_ENCONTRADO -> "El paciente no fue encontrado";
            case DOCTOR_NO_ENCONTRADO -> "El doctor no fue encontrado";
            case ESPECIALIDAD_NO_ENCONTRADA -> "La especialidad no fue encontrada";
            case CITA_PASADA -> "No se puede modificar una cita pasada";
            case CITA_CANCELADA -> "La cita ya fue cancelada";
            case CITA_COMPLETADA -> "La cita ya fue completada";
        };

        ErrorResponse errorResponse = new ErrorResponse(
                ex.getErrorCode(),
                ex.getMessage(),
                userMessage,
                LocalDateTime.now(),
                null
        );

        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * Maneja excepciones específicas de disponibilidad.
     */
    @ExceptionHandler(DisponibilidadException.class)
    public ResponseEntity<ErrorResponse> handleDisponibilidadException(DisponibilidadException ex) {
        String userMessage = switch (ex.getErrorType()) {
            case DOCTOR_NO_ENCONTRADO -> "El doctor no fue encontrado";
            case DISPONIBILIDAD_NO_ENCONTRADA -> "No se encontró disponibilidad para el doctor";
            case HORARIO_NO_DISPONIBLE -> "El horario seleccionado no está disponible";
            case CONFLICTO_HORARIO -> "Hay un conflicto con el horario seleccionado";
            case FECHA_INVALIDA -> "La fecha proporcionada no es válida";
            case DOCTOR_SIN_DISPONIBILIDAD -> "El doctor no tiene disponibilidad registrada";
            case DISPONIBILIDAD_YA_EXISTE -> "Ya existe una disponibilidad para ese horario";
        };

        ErrorResponse errorResponse = new ErrorResponse(
                ex.getErrorCode(),
                ex.getMessage(),
                userMessage,
                LocalDateTime.now(),
                null
        );

        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * Maneja excepciones específicas de tipos de cita.
     */
    @ExceptionHandler(TipoCitaException.class)
    public ResponseEntity<ErrorResponse> handleTipoCitaException(TipoCitaException ex) {
        String userMessage = switch (ex.getErrorType()) {
            case TIPO_CITA_NO_ENCONTRADO -> "El tipo de cita no fue encontrado";
            case TIPO_CITA_YA_EXISTE -> "Ya existe un tipo de cita con ese nombre";
            case NOMBRE_INVALIDO -> "El nombre del tipo de cita no es válido";
            case DURACION_INVALIDA -> "La duración del tipo de cita no es válida";
            case TIPO_CITA_EN_USO -> "No se puede eliminar un tipo de cita que está en uso";
        };

        ErrorResponse errorResponse = new ErrorResponse(
                ex.getErrorCode(),
                ex.getMessage(),
                userMessage,
                LocalDateTime.now(),
                null
        );

        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * Maneja excepciones específicas de generación de PDFs.
     */
    @ExceptionHandler(PdfException.class)
    public ResponseEntity<ErrorResponse> handlePdfException(PdfException ex) {
        String userMessage = switch (ex.getErrorType()) {
            case ERROR_GENERACION_PDF -> "Error al generar el documento PDF";
            case ERROR_HEADER_FOOTER -> "Error al agregar encabezado o pie de página";
            case ERROR_AGREGAR_CONTENIDO -> "Error al agregar contenido al documento";
            case ERROR_GUARDAR_PDF -> "Error al guardar el documento PDF";
            case DATOS_INSUFICIENTES -> "Datos insuficientes para generar el documento";
            case FORMATO_INVALIDO -> "El formato del documento no es válido";
        };

        ErrorResponse errorResponse = new ErrorResponse(
                ex.getErrorCode(),
                ex.getMessage(),
                userMessage,
                LocalDateTime.now(),
                null
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Maneja excepciones de conflictos (recursos ya existentes).
     */
    @ExceptionHandler({EmailAlreadyExistsException.class, UserAlreadyExistsException.class})
    public ResponseEntity<ErrorResponse> handleConflictException(RuntimeException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                "CONFLICT",
                ex.getMessage(),
                "El recurso ya existe",
                LocalDateTime.now(),
                null
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    /**
     * Maneja excepciones de operaciones de base de datos.
     */
    @ExceptionHandler(DatabaseOperationException.class)
    public ResponseEntity<ErrorResponse> handleDatabaseException(DatabaseOperationException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                "DATABASE_ERROR",
                ex.getMessage(),
                "Error en la operación de base de datos",
                LocalDateTime.now(),
                null
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Maneja excepciones de envío de emails.
     */
    @ExceptionHandler(EmailSendingException.class)
    public ResponseEntity<ErrorResponse> handleEmailException(EmailSendingException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                "EMAIL_ERROR",
                ex.getMessage(),
                "Error al enviar el correo electrónico",
                LocalDateTime.now(),
                null
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Maneja excepciones genéricas no manejadas.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                "INTERNAL_SERVER_ERROR",
                "Error interno del servidor",
                "Ha ocurrido un error inesperado",
                LocalDateTime.now(),
                null
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Clase para respuestas de error consistentes.
     */
    public static class ErrorResponse {
        private final String errorCode;
        private final String message;
        private final String userMessage;
        private final LocalDateTime timestamp;
        private final Map<String, String> details;

        public ErrorResponse(String errorCode, String message, String userMessage, 
                           LocalDateTime timestamp, Map<String, String> details) {
            this.errorCode = errorCode;
            this.message = message;
            this.userMessage = userMessage;
            this.timestamp = timestamp;
            this.details = details;
        }

        // Getters
        public String getErrorCode() { return errorCode; }
        public String getMessage() { return message; }
        public String getUserMessage() { return userMessage; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public Map<String, String> getDetails() { return details; }
    }
}
