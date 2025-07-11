package edu.uniquindio.dentalmanagementsystembackend.exception;

/**
 * Excepción específica para errores relacionados con citas médicas.
 */
public class CitaException extends BusinessException {
    
    public enum CitaErrorType {
        CITA_NO_ENCONTRADA("CITA_NOT_FOUND"),
        CITA_YA_EXISTE("CITA_ALREADY_EXISTS"),
        DOCTOR_NO_DISPONIBLE("DOCTOR_NOT_AVAILABLE"),
        HORARIO_NO_DISPONIBLE("SCHEDULE_NOT_AVAILABLE"),
        PACIENTE_NO_ENCONTRADO("PATIENT_NOT_FOUND"),
        DOCTOR_NO_ENCONTRADO("DOCTOR_NOT_FOUND"),
        ESPECIALIDAD_NO_ENCONTRADA("SPECIALTY_NOT_FOUND"),
        CITA_PASADA("APPOINTMENT_IN_PAST"),
        CITA_CANCELADA("APPOINTMENT_CANCELLED"),
        CITA_COMPLETADA("APPOINTMENT_COMPLETED");
        
        private final String errorCode;
        
        CitaErrorType(String errorCode) {
            this.errorCode = errorCode;
        }
        
        public String getErrorCode() {
            return errorCode;
        }
    }
    
    private final CitaErrorType errorType;
    private final Long citaId;
    
    public CitaException(String message, CitaErrorType errorType) {
        super(message, errorType.getErrorCode());
        this.errorType = errorType;
        this.citaId = null;
    }
    
    public CitaException(String message, CitaErrorType errorType, Long citaId) {
        super(message, errorType.getErrorCode());
        this.errorType = errorType;
        this.citaId = citaId;
    }
    
    public CitaException(String message, CitaErrorType errorType, String userMessage) {
        super(message, errorType.getErrorCode(), userMessage);
        this.errorType = errorType;
        this.citaId = null;
    }
    
    public CitaErrorType getErrorType() {
        return errorType;
    }
    
    public Long getCitaId() {
        return citaId;
    }
} 