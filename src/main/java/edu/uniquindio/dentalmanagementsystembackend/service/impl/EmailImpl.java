package edu.uniquindio.dentalmanagementsystembackend.service.impl;

import edu.uniquindio.dentalmanagementsystembackend.dto.account.EmailDTO;
import edu.uniquindio.dentalmanagementsystembackend.service.Interfaces.EmailService;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

// Anotación que indica que esta clase es un servicio de Spring
@Service
public class EmailImpl implements EmailService {

    // Nombre de usuario del servidor SMTP
    private final String SMTP_USERNAME = "unieventosproyect@gmail.com";
    // Contraseña del servidor SMTP
    private final String SMTP_PASSWORD = "fyncswwbtqwubuja";

    /**
     * Envía un email electrónico.
     *
     * @param emailDTO Data Transfer Object que contiene la información del email electrónico a enviar.
     * @throws Exception
     */
    @Override
    @Async
    public void sendMail(EmailDTO emailDTO) throws Exception {
        // Construcción del email utilizando el EmailBuilder
        Email email = EmailBuilder.startingBlank()
                .from(SMTP_USERNAME)
                .to(emailDTO.recipient())
                .withSubject(emailDTO.issue())
                .withHTMLText(emailDTO.body())
                .buildEmail();

        // Envío del email utilizando el Mailer
        try (Mailer mailer = MailerBuilder
                .withSMTPServer("smtp.gmail.com", 587, SMTP_USERNAME, SMTP_PASSWORD)
                .withTransportStrategy(TransportStrategy.SMTP_TLS)
                .withDebugLogging(true)
                .buildMailer()) {
            mailer.sendMail(email);
        }
    }

    /**
     * Envía un código QR por email electrónico.
     *
     * @param email La dirección de email electrónico a la que se enviará el QR.
     * @param qrUrl La URL de la imagen del código QR.
     */
    @Override
    @Async
    public void sendQrByEmail(String email, String qrUrl) {
        // Construcción del mensaje HTML con el código QR
        String htmlMessage = "<html><body>" +
                "<p>Estimado usuario,</p>" +
                "<p>Gracias por su compra. A continuación encontrará el código QR de su orden:</p>" +
                "<img src=\"" + qrUrl + "\" alt=\"Código QR\" style=\"display:block; max-width:100%; height:auto;\" />" +
                "<p>Atentamente,<br/>El equipo de UniEventos</p>" +
                "</body></html>";

        // Envío del email con el código QR
        try {
            sendMail(new EmailDTO(email, "Código QR de su Orden", htmlMessage));
        } catch (Exception e) {
            e.printStackTrace(); // Manejo de excepciones, podrías usar un logger aquí
        }
    }

    /**
     * Envía un código de validación por email electrónico.
     *
     * @param email La dirección de email electrónico a la que se enviará el código de validación.
     * @param validationCode El código de validación a enviar.
     * @throws Exception
     */
    @Async
    @Override
    public void sendCodevalidation(String email, String validationCode) throws Exception {
        // Construcción del mensaje HTML con el código de validación
        String htmlMessage = "<html><body>" +
                "<p>Estimado usuario,</p>" +
                "<p>Gracias por registrarse en nuestra plataforma. Para activar su cuenta, por favor utilice el siguiente código de activación:</p>" +
                "<h3>Código de activación: " + validationCode + "</h3>" +
                "<p>Este código es válido por 15 minutos.</p>" +
                "<p>Si usted no solicitó este registro, por favor ignore este email.</p>" +
                "<p>Atentamente,<br/>El equipo de OdontoLogic</p>" +
                "</body></html>";

        // Envío del email con el código de activación
        sendMail(new EmailDTO(email, "\"Activación de cuenta\"", htmlMessage));
    }

    /**
     * Envía un código de recuperación de contraseña por email electrónico.
     *
     * @param email La dirección de email electrónico a la que se enviará el código de recuperación.
     * @param recoveryCode El código de validación de la contraseña a enviar.
     * @throws Exception
     */
    @Override
    @Async
    public void sendRecoveryCode(String email, String recoveryCode) throws Exception {
        // Construcción del mensaje HTML con el código de recuperación
        String htmlMessage = "<html><body>" +
                "<p>Estimado usuario,</p>" +
                "<p>Ha solicitado recuperar su contraseña. Utilice el siguiente código de recuperación para restablecer su contraseña:</p>" +
                "<h3>Código de recuperación: " + recoveryCode + "</h3>" +
                "<p>Este código es válido por 15 minutos.</p>" +
                "<p>Si usted no solicitó esta recuperación, por favor ignore este email.</p>" +
                "<p>Atentamente,<br/>El equipo de OdontoLogic</p>" +
                "</body></html>";

        // Envío del email con el código de recuperación
        sendMail(new EmailDTO(email, "Recuperación de contraseña", htmlMessage));
    }

    @Override
    @Async
    public void enviarCorreoCita(String email, String nombreOdontologo, String fechaHora) throws Exception {
        // Generación del contenido HTML para la confirmación de cita
        String htmlMessage = "<html><body>" +
                "<p>Estimado usuario,</p>" +
                "<p>Su cita con el Dr. " + nombreOdontologo + " ha sido programada exitosamente.</p>" +
                "<p><strong>Fecha y hora:</strong> " + fechaHora + "</p>" +
                "<p>Si necesita cancelar o modificar el tipo de servicio, puede hacerlo 24 horas antes.</p>" +
                "<p>Para realizar cambios, por favor comuníquese con nuestra clínica.</p>" +
                "<p>Atentamente,<br/>El equipo de OdontoLogic</p>" +
                "</body></html>";

        // Envío del correo de confirmación de cita
        sendMail(new EmailDTO(email, "Confirmación de Cita Odontológica", htmlMessage));
    }


}