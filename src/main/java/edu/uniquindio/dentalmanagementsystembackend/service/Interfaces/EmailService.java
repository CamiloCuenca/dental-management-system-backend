package edu.uniquindio.dentalmanagementsystembackend.service.Interfaces;

import edu.uniquindio.dentalmanagementsystembackend.dto.EmailDTO;

public interface EmailService {

    /**
     * Envía un email electrónico.
     *
     * @param emailDTO Data Transfer Object que contiene la información del email electrónico a enviar.
     * @throws Exception Si ocurre un error al enviar el email electrónico.
     */
    void sendMail(EmailDTO emailDTO) throws Exception;

    /**
     * Envía un código QR por email electrónico.
     *
     * @param email La dirección de email electrónico a la que se enviará el QR.
     * @param qrImageUrl La URL de la imagen del código QR.
     * @throws Exception Si ocurre un error al enviar el QR por email electrónico.
     */
    void sendQrByEmail(String email, String qrImageUrl) throws Exception;

    /**
     * Envía un código de validación por email electrónico.
     *
     * @param email La dirección de email electrónico a la que se enviará el código de validación.
     * @param validationCode El código de validación a enviar.
     * @throws Exception Si ocurre un error al enviar el código de validación.
     */
    void sendCodevalidation(String email, String validationCode) throws Exception;

    /**
     * Envía un código de recuperación de contraseña por email electrónico.
     *
     * @param email La dirección de email electrónico a la que se enviará el código de recuperación.
     * @param passwordValidationCode El código de validación de la contraseña a enviar.
     * @throws Exception Si ocurre un error al enviar el código de recuperación.
     */
    void sendRecoveryCode(String email, String passwordValidationCode) throws Exception;

}
