package edu.uniquindio.dentalmanagementsystembackend.service.Interfaces;

import edu.uniquindio.dentalmanagementsystembackend.dto.account.EmailDTO;
import java.time.LocalDateTime;

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
     * @param qrUrl La URL de la imagen del código QR.
     */
    void sendQrByEmail(String email, String qrUrl);

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
     * @param recoveryCode El código de validación de la contraseña a enviar.
     * @throws Exception Si ocurre un error al enviar el código de recuperación.
     */
    void sendRecoveryCode(String email, String recoveryCode) throws Exception;

    /**
     * Envía un correo de confirmación de cita
     * @param email Email del paciente
     * @param nombreOdontologo Nombre del odontólogo
     * @param fechaHora Fecha y hora de la cita
     */
    void enviarCorreoConfirmacionCita(String email, String nombreOdontologo, LocalDateTime fechaHora);

    /**
     * Envía un correo de cancelación de cita
     * @param email Email del paciente
     * @param nombreOdontologo Nombre del odontólogo
     * @param fechaHora Fecha y hora de la cita
     */
    void enviarCorreoCancelacionCita(String email, String nombreOdontologo, LocalDateTime fechaHora);

    /**
     * Envía un correo de reprogramación de cita
     * @param email Email del paciente
     * @param nombreOdontologo Nombre del odontólogo
     * @param fechaHora Fecha y hora de la cita
     */
    void enviarCorreoReprogramacionCita(String email, String nombreOdontologo, LocalDateTime fechaHora);

    /**
     * Envía un correo de recordatorio de cita
     * @param email Email del paciente
     * @param nombreOdontologo Nombre del odontólogo
     * @param fechaHora Fecha y hora de la cita
     */
    void enviarCorreoRecordatorioCita(String email, String nombreOdontologo, LocalDateTime fechaHora);

    /**
     * Envía un correo de cita completada
     * @param email Email del paciente
     * @param nombreOdontologo Nombre del odontólogo
     * @param fechaHora Fecha y hora de la cita
     */
    void enviarCorreoCitaCompletada(String email, String nombreOdontologo, LocalDateTime fechaHora);

    /**
     * Envía un correo de cita de emergencia
     * @param email Email del paciente
     * @param nombreOdontologo Nombre del odontólogo
     * @param fechaHora Fecha y hora de la cita
     */
    void enviarCorreoCitaEmergencia(String email, String nombreOdontologo, LocalDateTime fechaHora);

    void enviarCorreoCita(String email, String nombreOdontologo, String fechaHora)  throws Exception;

}
