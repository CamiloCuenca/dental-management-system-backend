package edu.uniquindio.dentalmanagementsystembackend.exception;

/**
 * Excepción específica para errores relacionados con la generación de PDFs.
 */
public class PdfException extends BusinessException {
    
    public enum PdfErrorType {
        ERROR_GENERACION_PDF("PDF_GENERATION_ERROR"),
        ERROR_HEADER_FOOTER("HEADER_FOOTER_ERROR"),
        ERROR_AGREGAR_CONTENIDO("ADD_CONTENT_ERROR"),
        ERROR_GUARDAR_PDF("SAVE_PDF_ERROR"),
        DATOS_INSUFICIENTES("INSUFFICIENT_DATA"),
        FORMATO_INVALIDO("INVALID_FORMAT");
        
        private final String errorCode;
        
        PdfErrorType(String errorCode) {
            this.errorCode = errorCode;
        }
        
        public String getErrorCode() {
            return errorCode;
        }
    }
    
    private final PdfErrorType errorType;
    private final String documentType;
    
    public PdfException(String message, PdfErrorType errorType) {
        super(message, errorType.getErrorCode());
        this.errorType = errorType;
        this.documentType = null;
    }
    
    public PdfException(String message, PdfErrorType errorType, String documentType) {
        super(message, errorType.getErrorCode());
        this.errorType = errorType;
        this.documentType = documentType;
    }
    
    public PdfException(String message, PdfErrorType errorType, String userMessage, String documentType) {
        super(message, errorType.getErrorCode(), userMessage);
        this.errorType = errorType;
        this.documentType = documentType;
    }
    
    public PdfErrorType getErrorType() {
        return errorType;
    }
    
    public String getDocumentType() {
        return documentType;
    }
} 