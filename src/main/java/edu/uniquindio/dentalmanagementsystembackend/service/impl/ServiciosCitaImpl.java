package edu.uniquindio.dentalmanagementsystembackend.service.impl;

import edu.uniquindio.dentalmanagementsystembackend.Enum.EstadoCitas;
import edu.uniquindio.dentalmanagementsystembackend.Enum.Rol;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.CrearCitaDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.EditarCitaAdminDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.EditarCitaPacienteDTO;
import edu.uniquindio.dentalmanagementsystembackend.entity.Account.User;
import edu.uniquindio.dentalmanagementsystembackend.entity.Cita;
import edu.uniquindio.dentalmanagementsystembackend.entity.DisponibilidadDoctor;
import edu.uniquindio.dentalmanagementsystembackend.entity.Especialidad;
import edu.uniquindio.dentalmanagementsystembackend.entity.TipoCita;
import edu.uniquindio.dentalmanagementsystembackend.exception.HistorialException;
import edu.uniquindio.dentalmanagementsystembackend.repository.CitasRepository;
import edu.uniquindio.dentalmanagementsystembackend.repository.CuentaRepository;
import edu.uniquindio.dentalmanagementsystembackend.repository.DisponibilidadDoctorRepository;
import edu.uniquindio.dentalmanagementsystembackend.repository.TipoCitaRepository;
import edu.uniquindio.dentalmanagementsystembackend.repository.UserRepository;
import edu.uniquindio.dentalmanagementsystembackend.service.Interfaces.EmailService;
import edu.uniquindio.dentalmanagementsystembackend.service.Interfaces.ServiciosCitas;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

/**
 * Implementación del servicio de gestión de citas dentales.
 * Esta clase maneja toda la lógica de negocio relacionada con las citas,
 * incluyendo su creación, consulta, modificación y cancelación.
 */
@Transactional
@Service
public class ServiciosCitaImpl implements ServiciosCitas {

    // Repositorio para operaciones CRUD de citas
    @Autowired
    private CitasRepository citasRepository;

    // Repositorio para operaciones relacionadas con cuentas de usuario
    @Autowired
    private CuentaRepository cuentaRepository;

    // Repositorio para operaciones CRUD de usuarios
    @Autowired
    private UserRepository userRepository;

    // Repositorio para operaciones CRUD de disponibilidad de doctores
    @Autowired
    private DisponibilidadDoctorRepository disponibilidadDoctorRepository;

    // Repositorio para operaciones CRUD de tipos de cita
    @Autowired
    private TipoCitaRepository tipoCitaRepository;

    // Servicio para envío de correos electrónicos
    @Autowired
    private EmailService emailService;

    @Override
    public Cita crearCita(CrearCitaDTO dto) {
        // Validar que el paciente existe
        User paciente = userRepository.findById(dto.pacienteId())
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        // Validar que el odontólogo existe
        User odontologo = userRepository.findById(dto.odontologoId())
                .orElseThrow(() -> new RuntimeException("Odontólogo no encontrado"));

        // Validar que el odontólogo tiene el rol correcto
        if (odontologo.getAccount().getRol() != Rol.DOCTOR) {
            throw new RuntimeException("El usuario seleccionado no es un odontólogo");
        }

        // Obtener el tipo de cita especificado
        TipoCita tipoCita = tipoCitaRepository.findById(dto.tipoCitaId())
                .orElseThrow(() -> new RuntimeException("Tipo de cita no encontrado"));

        // Validar que el odontólogo tiene la especialidad requerida para el tipo de cita
        boolean tieneEspecialidad = odontologo.getEspecialidades().stream()
                .anyMatch(esp -> esp.getId().equals(tipoCita.getEspecialidadRequerida().getId()));
        
        if (!tieneEspecialidad) {
            throw new RuntimeException("El odontólogo no tiene la especialidad requerida para este tipo de cita");
        }

        // Convertir Instant a LocalDateTime para la validación de disponibilidad
        LocalDateTime fechaHoraLocal = dto.fechaHora().atZone(ZoneId.systemDefault()).toLocalDateTime();

        // Validar disponibilidad del doctor
        boolean doctorDisponible = disponibilidadDoctorRepository.findAll().stream()
                .anyMatch(disp -> disp.getDoctor().getIdNumber().equals(odontologo.getIdNumber()) &&
                        disp.getHoraInicio().isBefore(fechaHoraLocal.toLocalTime()) &&
                        disp.getHoraFin().isAfter(fechaHoraLocal.toLocalTime()) &&
                        disp.getDiaSemana() == fechaHoraLocal.getDayOfWeek());

        if (!doctorDisponible) {
            throw new RuntimeException("El doctor no está disponible en la fecha y hora seleccionada");
        }

        // Crear la cita
        Cita cita = new Cita(
                paciente,
                odontologo,
                dto.fechaHora(),
                EstadoCitas.PENDIENTE,
                tipoCita
        );

        // Guardar la cita
        Cita citaGuardada = citasRepository.save(cita);

        // Enviar notificación por correo
        try {
            emailService.enviarCorreoConfirmacionCita(
                    paciente.getAccount().getEmail(),
                    odontologo.getName() + " " + odontologo.getLastName(),
                    fechaHoraLocal
            );
        } catch (Exception e) {
            // Log del error pero no interrumpir el flujo
            System.err.println("Error al enviar correo: " + e.getMessage());
        }

        return citaGuardada;
    }
}