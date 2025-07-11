package edu.uniquindio.dentalmanagementsystembackend.exception;

/**
 * Excepción específica para errores relacionados con tipos de cita.
 */
public class TipoCitaException extends BusinessException {
    
    public enum TipoCitaErrorType {
        TIPO_CITA_NO_ENCONTRADO("APPOINTMENT_TYPE_NOT_FOUND"),
        TIPO_CITA_YA_EXISTE("APPOINTMENT_TYPE_ALREADY_EXISTS"),
        NOMBRE_INVALIDO("INVALID_NAME"),
        DURACION_INVALIDA("INVALID_DURATION"),
        TIPO_CITA_EN_USO("APPOINTMENT_TYPE_IN_USE");
        
        private final String errorCode;
        
        TipoCitaErrorType(String errorCode) {
            this.errorCode = errorCode;
        }
        
        public String getErrorCode() {
            return errorCode;
        }
    }
    
    private final TipoCitaErrorType errorType;
    private final Long tipoCitaId;
    
    public TipoCitaException(String message, TipoCitaErrorType errorType) {
        super(message, errorType.getErrorCode());
        this.errorType = errorType;
        this.tipoCitaId = null;
    }
    
    public TipoCitaException(String message, TipoCitaErrorType errorType, Long tipoCitaId) {
        super(message, errorType.getErrorCode());
        this.errorType = errorType;
        this.tipoCitaId = tipoCitaId;
    }
    
    public TipoCitaException(String message, TipoCitaErrorType errorType, String userMessage) {
        super(message, errorType.getErrorCode(), userMessage);
        this.errorType = errorType;
        this.tipoCitaId = null;
    }
    
    public TipoCitaErrorType getErrorType() {
        return errorType;
    }
    
    public Long getTipoCitaId() {
        return tipoCitaId;
    }
} 