package edu.uniquindio.dentalmanagementsystembackend.exception;

/**
 * Excepción específica para errores relacionados con la disponibilidad de doctores.
 */
public class DisponibilidadException extends BusinessException {
    
    public enum DisponibilidadErrorType {
        DOCTOR_NO_ENCONTRADO("DOCTOR_NOT_FOUND"),
        DISPONIBILIDAD_NO_ENCONTRADA("AVAILABILITY_NOT_FOUND"),
        HORARIO_NO_DISPONIBLE("SCHEDULE_NOT_AVAILABLE"),
        CONFLICTO_HORARIO("SCHEDULE_CONFLICT"),
        FECHA_INVALIDA("INVALID_DATE"),
        DOCTOR_SIN_DISPONIBILIDAD("DOCTOR_NO_AVAILABILITY"),
        DISPONIBILIDAD_YA_EXISTE("AVAILABILITY_ALREADY_EXISTS");
        
        private final String errorCode;
        
        DisponibilidadErrorType(String errorCode) {
            this.errorCode = errorCode;
        }
        
        public String getErrorCode() {
            return errorCode;
        }
    }
    
    private final DisponibilidadErrorType errorType;
    private final String doctorId;
    
    public DisponibilidadException(String message, DisponibilidadErrorType errorType) {
        super(message, errorType.getErrorCode());
        this.errorType = errorType;
        this.doctorId = null;
    }
    
    public DisponibilidadException(String message, DisponibilidadErrorType errorType, String doctorId) {
        super(message, errorType.getErrorCode());
        this.errorType = errorType;
        this.doctorId = doctorId;
    }
    
    public DisponibilidadException(String message, DisponibilidadErrorType errorType, String userMessage, String doctorId) {
        super(message, errorType.getErrorCode(), userMessage);
        this.errorType = errorType;
        this.doctorId = doctorId;
    }
    
    public DisponibilidadErrorType getErrorType() {
        return errorType;
    }
    
    public String getDoctorId() {
        return doctorId;
    }
} 