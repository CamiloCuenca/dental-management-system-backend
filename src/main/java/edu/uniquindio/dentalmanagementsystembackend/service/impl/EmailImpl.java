package edu.uniquindio.dentalmanagementsystembackend.service.impl;

import edu.uniquindio.dentalmanagementsystembackend.dto.account.EmailDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.email.CitaEmailDTO;
import edu.uniquindio.dentalmanagementsystembackend.service.Interfaces.EmailService;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Anotación que indica que esta clase es un servicio de Spring
@Service
public class EmailImpl implements EmailService {

    // Nombre de usuario del servidor SMTP
    private final String SMTP_USERNAME = "unieventosproyect@gmail.com";
    // Contraseña del servidor SMTP
    private final String SMTP_PASSWORD = "fyncswwbtqwubuja";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

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
        String htmlMessage = """
            <html>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                <div style="max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 5px;">
                    <h2 style="color: #2c3e50;">Código QR de su Orden</h2>
                    <p>Estimado usuario,</p>
                    <p>Gracias por su compra. A continuación encontrará el código QR de su orden:</p>
                    <div style="text-align: center; margin: 20px 0;">
                        <img src="%s" alt="Código QR" style="display:block; max-width:100%; height:auto; margin: 0 auto;" />
                    </div>
                    <p>Atentamente,<br/>El equipo de UniEventos</p>
                    <hr style="border: 1px solid #eee; margin: 20px 0;">
                    <p style="font-size: 12px; color: #777;">Este es un correo automático, por favor no responda.</p>
                </div>
            </body>
            </html>
            """.formatted(qrUrl);

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
        String htmlMessage = """
            <html>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                <div style="max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 5px;">
                    <h2 style="color: #2c3e50;">Activación de cuenta</h2>
                    <p>Estimado usuario,</p>
                    <p>Gracias por registrarse en nuestra plataforma. Para activar su cuenta, por favor utilice el siguiente código de activación:</p>
                    <div style="background-color: #f8f9fa; padding: 15px; border-radius: 5px; text-align: center; margin: 20px 0;">
                        <h3 style="margin: 0; color: #2c3e50;">Código de activación: %s</h3>
                    </div>
                    <p>Este código es válido por 15 minutos.</p>
                    <p>Si usted no solicitó este registro, por favor ignore este email.</p>
                    <p>Atentamente,<br/>El equipo de OdontoLogic</p>
                    <hr style="border: 1px solid #eee; margin: 20px 0;">
                    <p style="font-size: 12px; color: #777;">Este es un correo automático, por favor no responda.</p>
                </div>
            </body>
            </html>
            """.formatted(validationCode);

        // Envío del email con el código de activación
        sendMail(new EmailDTO(email, "Activación de cuenta", htmlMessage));
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
        String htmlMessage = """
            <html>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                <div style="max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 5px;">
                    <h2 style="color: #2c3e50;">Recuperación de contraseña</h2>
                    <p>Estimado usuario,</p>
                    <p>Ha solicitado recuperar su contraseña. Utilice el siguiente código de recuperación para restablecer su contraseña:</p>
                    <div style="background-color: #f8f9fa; padding: 15px; border-radius: 5px; text-align: center; margin: 20px 0;">
                        <h3 style="margin: 0; color: #2c3e50;">Código de recuperación: %s</h3>
                    </div>
                    <p>Este código es válido por 15 minutos.</p>
                    <p>Si usted no solicitó esta recuperación, por favor ignore este email.</p>
                    <p>Atentamente,<br/>El equipo de OdontoLogic</p>
                    <hr style="border: 1px solid #eee; margin: 20px 0;">
                    <p style="font-size: 12px; color: #777;">Este es un correo automático, por favor no responda.</p>
                </div>
            </body>
            </html>
            """.formatted(recoveryCode);

        // Envío del email con el código de recuperación
        sendMail(new EmailDTO(email, "Recuperación de contraseña", htmlMessage));
    }

    @Override
    @Async
    public void enviarCorreoConfirmacionCita(String email, String nombreOdontologo, LocalDateTime fechaHora) {
        String htmlMessage = """
            <html>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                <div style="max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 5px;">
                    <h2 style="color: #2c3e50;">Confirmación de Cita Odontológica</h2>
                    <p>Estimado paciente,</p>
                    <p>Su cita con el Dr. %s ha sido confirmada exitosamente.</p>
                    <ul>
                        <li><strong>Fecha y hora:</strong> %s</li>
                    </ul>
                    <p>Por favor, asegúrese de llegar 15 minutos antes de su cita.</p>
                    <p>Si necesita cancelar o reprogramar su cita, puede hacerlo 24 horas antes.</p>
                    <p>Atentamente,<br/>El equipo de OdontoLogic</p>
                    <hr style="border: 1px solid #eee; margin: 20px 0;">
                    <p style="font-size: 12px; color: #777;">Este es un correo automático, por favor no responda.</p>
                </div>
            </body>
            </html>
            """.formatted(nombreOdontologo, fechaHora.format(formatter));

        try {
            sendMail(new EmailDTO(email, "Confirmación de Cita Odontológica", htmlMessage));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    @Async
    public void enviarCorreoCancelacionCita(String email, String nombreOdontologo, LocalDateTime fechaHora) {
        String htmlMessage = """
            <html>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                <div style="max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 5px;">
                    <h2 style="color: #2c3e50;">Cancelación de Cita Odontológica</h2>
                    <p>Estimado paciente,</p>
                    <p>Su cita con el Dr. %s ha sido cancelada.</p>
                    <ul>
                        <li><strong>Fecha y hora original:</strong> %s</li>
                    </ul>
                    <p>Si desea programar una nueva cita, por favor contáctenos.</p>
                    <p>Atentamente,<br/>El equipo de OdontoLogic</p>
                    <hr style="border: 1px solid #eee; margin: 20px 0;">
                    <p style="font-size: 12px; color: #777;">Este es un correo automático, por favor no responda.</p>
                </div>
            </body>
            </html>
            """.formatted(nombreOdontologo, fechaHora.format(formatter));

        try {
            sendMail(new EmailDTO(email, "Cancelación de Cita Odontológica", htmlMessage));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    @Async
    public void enviarCorreoReprogramacionCita(String email, String nombreOdontologo, LocalDateTime fechaHora) {
        String htmlMessage = """
            <html>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                <div style="max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 5px;">
                    <h2 style="color: #2c3e50;">Reprogramación de Cita Odontológica</h2>
                    <p>Estimado paciente,</p>
                    <p>Su cita con el Dr. %s ha sido reprogramada.</p>
                    <ul>
                        <li><strong>Nueva fecha y hora:</strong> %s</li>
                    </ul>
                    <p>Por favor, asegúrese de llegar 15 minutos antes de su cita.</p>
                    <p>Si necesita cancelar o reprogramar su cita, puede hacerlo 24 horas antes.</p>
                    <p>Atentamente,<br/>El equipo de OdontoLogic</p>
                    <hr style="border: 1px solid #eee; margin: 20px 0;">
                    <p style="font-size: 12px; color: #777;">Este es un correo automático, por favor no responda.</p>
                </div>
            </body>
            </html>
            """.formatted(nombreOdontologo, fechaHora.format(formatter));

        try {
            sendMail(new EmailDTO(email, "Reprogramación de Cita Odontológica", htmlMessage));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    @Async
    public void enviarCorreoRecordatorioCita(String email, String nombreOdontologo, LocalDateTime fechaHora) {
        String htmlMessage = """
            <html>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                <div style="max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 5px;">
                    <h2 style="color: #2c3e50;">Recordatorio de Cita Odontológica</h2>
                    <p>Estimado paciente,</p>
                    <p>Este es un recordatorio de su próxima cita con el Dr. %s.</p>
                    <ul>
                        <li><strong>Fecha y hora:</strong> %s</li>
                    </ul>
                    <p>Por favor, asegúrese de llegar 15 minutos antes de su cita.</p>
                    <p>Si necesita cancelar o reprogramar su cita, puede hacerlo 24 horas antes.</p>
                    <p>Atentamente,<br/>El equipo de OdontoLogic</p>
                    <hr style="border: 1px solid #eee; margin: 20px 0;">
                    <p style="font-size: 12px; color: #777;">Este es un correo automático, por favor no responda.</p>
                </div>
            </body>
            </html>
            """.formatted(nombreOdontologo, fechaHora.format(formatter));

        try {
            sendMail(new EmailDTO(email, "Recordatorio de Cita Odontológica", htmlMessage));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    @Async
    public void enviarCorreoCitaCompletada(String email, String nombreOdontologo, LocalDateTime fechaHora) {
        String htmlMessage = """
            <html>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                <div style="max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 5px;">
                    <h2 style="color: #2c3e50;">Cita Completada</h2>
                    <p>Estimado paciente,</p>
                    <p>Su cita con el Dr. %s ha sido marcada como completada.</p>
                    <ul>
                        <li><strong>Fecha y hora:</strong> %s</li>
                    </ul>
                    <p>Gracias por confiar en nuestros servicios.</p>
                    <p>Si tiene alguna pregunta o necesita programar una nueva cita, no dude en contactarnos.</p>
                    <p>Atentamente,<br/>El equipo de OdontoLogic</p>
                    <hr style="border: 1px solid #eee; margin: 20px 0;">
                    <p style="font-size: 12px; color: #777;">Este es un correo automático, por favor no responda.</p>
                </div>
            </body>
            </html>
            """.formatted(nombreOdontologo, fechaHora.format(formatter));

        try {
            sendMail(new EmailDTO(email, "Cita Completada", htmlMessage));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    @Async
    public void enviarCorreoCita(CitaEmailDTO dto) throws Exception {
        String htmlMessage = """
            <html>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                <div style="max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 5px;">
                    <h2 style="color: #2c3e50;">Confirmación de Cita Odontológica</h2>
                    <p>Estimado(a) %s,</p>
                    <p>Su cita ha sido programada exitosamente con los siguientes detalles:</p>
                    <ul>
                        <li><strong>Doctor:</strong> Dr. %s</li>
                        <li><strong>Fecha y hora:</strong> %s</li>
                        <li><strong>Tipo de cita:</strong> %s</li>
                    </ul>
                    <p>Si necesita cancelar o modificar su cita, puede hacerlo hasta 24 horas antes.</p>
                    <p>Para realizar cambios, por favor comuníquese con nuestra clínica.</p>
                    <hr style="border: 1px solid #eee; margin: 20px 0;">
                    <p style="font-size: 12px; color: #777;">Este es un correo automático, por favor no responda.</p>
                    <p>Atentamente,<br/>El equipo de OdontoLogic</p>
                </div>
            </body>
            </html>
            """.formatted(
                dto.nombrePaciente(),
                dto.nombreOdontologo(),
                dto.fechaHora(),
                dto.tipoCita()
            );

        sendMail(new EmailDTO(dto.emailPaciente(), "Confirmación de Cita Odontológica", htmlMessage));
    }


}