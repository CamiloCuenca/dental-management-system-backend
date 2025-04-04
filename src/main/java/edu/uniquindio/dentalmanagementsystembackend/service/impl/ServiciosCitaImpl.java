package edu.uniquindio.dentalmanagementsystembackend.service.impl;

import edu.uniquindio.dentalmanagementsystembackend.Enum.EstadoCitas;
import edu.uniquindio.dentalmanagementsystembackend.Enum.Rol;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.CrearCitaDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.EditarCitaAdminDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.EditarCitaPacienteDTO;
import edu.uniquindio.dentalmanagementsystembackend.entity.Account.User;
import edu.uniquindio.dentalmanagementsystembackend.entity.Cita;
import edu.uniquindio.dentalmanagementsystembackend.exception.HistorialException;
import edu.uniquindio.dentalmanagementsystembackend.repository.CitasRepository;
import edu.uniquindio.dentalmanagementsystembackend.repository.CuentaRepository;
import edu.uniquindio.dentalmanagementsystembackend.repository.UserRepository;
import edu.uniquindio.dentalmanagementsystembackend.service.Interfaces.EmailService;
import edu.uniquindio.dentalmanagementsystembackend.service.Interfaces.ServiciosCitas;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Implementación del servicio de gestión de citas dentales.
 * Esta clase maneja toda la lógica de negocio relacionada con las citas,
 * incluyendo su creación, consulta, modificación y cancelación.
 */
@Transactional
@Service
public class ServiciosCitaImpl implements ServiciosCitas {
    //hola

    // Repositorio para operaciones CRUD de citas
    @Autowired
    private CitasRepository citasRepository;

    // Repositorio para operaciones relacionadas con cuentas de usuario
    @Autowired
    private CuentaRepository cuentaRepository;

    // Repositorio para operaciones CRUD de usuarios
    @Autowired
    private UserRepository userRepository;

    // Servicio para envío de correos electrónicos
    @Autowired
    private EmailService emailService;


    /**
     * Crea una nueva cita en el sistema.
     * @param dto Objeto DTO que contiene la información necesaria para crear la cita
     * @return La cita creada y guardada en el sistema
     * @throws HistorialException si:
     *         - El paciente o doctor no existen o no tienen el rol correcto
     *         - La fecha es en el pasado
     *         - El paciente y doctor son la misma persona
     */
    @Override
    public Cita crearCita(CrearCitaDTO dto) {

        // Validar y obtener paciente
        User paciente = obtenerYValidarUsuario(dto.pacienteId(), Rol.PACIENTE, "Paciente");

        // Validar y obtener odontólogo
        User odontologo = obtenerYValidarUsuario(dto.odontologoId(), Rol.DOCTOR, "Odontólogo");

        if (dto.fechaHora().isBefore(Instant.now())) {
            throw new HistorialException("La fecha y hora de la cita no pueden estar en el pasado.");
        }

        if (paciente.getIdNumber().equals(odontologo.getIdNumber())) {
            throw new HistorialException("Un usuario no puede ser paciente y odontólogo en la misma cita.");
        }


        Cita cita = construirCita(paciente, odontologo, dto);

        return citasRepository.save(cita);
    }

    /**
     * Construye una nueva entidad Cita a partir de los datos proporcionados.
     * @param paciente Usuario con rol de paciente
     * @param odontologo Usuario con rol de doctor
     * @param dto DTO con la información de la cita
     * @return Nueva instancia de Cita con estado PENDIENTE
     */
    private Cita construirCita(User paciente, User odontologo, CrearCitaDTO dto) {
        return new Cita(
                paciente,
                odontologo,
                dto.fechaHora(),
                EstadoCitas.PENDIENTE,
                dto.tipoCita()
        );
    }

    /**
     * Obtiene y valida que un usuario exista y tenga el rol correcto.
     *
     * @param usuarioId   ID del usuario a validar
     * @param rolEsperado Rol que debe tener el usuario
     * @param tipoUsuario Descripción del tipo de usuario para mensajes de error
     * @return Usuario validado
     * @throws HistorialException Si el usuario no existe o no tiene el rol correcto
     */
    private User obtenerYValidarUsuario(Long usuarioId, Rol rolEsperado, String tipoUsuario) {
        User usuario = userRepository.findById(usuarioId)
                .orElseThrow(() -> new HistorialException(tipoUsuario + " no encontrado con ID: " + usuarioId));

        if (usuario.getAccount() == null || usuario.getAccount().getRol() != rolEsperado) {
            throw new HistorialException("El usuario con ID " + usuarioId + " no es un " + tipoUsuario.toLowerCase() + ".");
        }
        return usuario;
    }

    /**
     * Permite a un paciente cancelar su propia cita.
     * @param citaId Identificador de la cita a cancelar
     * @param userId Identificador del paciente que intenta cancelar la cita
     * @return La cita actualizada con estado CANCELADA
     * @throws HistorialException si:
     *         - La cita no existe
     *         - El usuario no es el dueño de la cita
     *         - La cita ya pasó
     *         - No hay 24 horas de anticipación
     */
    @Override
    public Cita cancelarCitaPaciente(Long citaId, Long userId) {
        // Obtener la cita existente
        Cita citaExistente = citasRepository.findById(citaId)
                .orElseThrow(() -> new HistorialException("No se encontró la cita con ID: " + citaId));

        // Validar que la cita no haya pasado
        if (citaExistente.getFechaHora().isBefore(Instant.now())) {
            throw new HistorialException("No se puede cancelar una cita que ya pasó.");
        }

        // Validar que la cancelación sea con al menos 24 horas de anticipación
        if (citaExistente.getFechaHora().isBefore(Instant.now().plusSeconds(24 * 60 * 60))) {
            throw new HistorialException("Las citas deben cancelarse con al menos 24 horas de anticipación.");
        }

        // Cambiar el estado de la cita a CANCELADA
        citaExistente.setEstado(EstadoCitas.CANCELADA);

        // Guardar los cambios
        Cita citaCancelada = citasRepository.save(citaExistente);

        // Aquí podrías agregar el envío de un correo electrónico de notificación si lo deseas
        // emailService.enviarCorreoCancelacion(citaCancelada);

        return citaCancelada;
    }

    /**
     * Permite a un administrador editar todos los aspectos de una cita.
     * @param dto DTO con la información actualizada de la cita
     * @return La cita actualizada
     * @throws HistorialException si:
     *         - La cita no existe
     *         - La nueva fecha está en el pasado
     *         - El nuevo paciente o doctor no existen o no tienen el rol correcto
     *         - El nuevo paciente y doctor son la misma persona
     */
    @Override
    public Cita editarCitaAdmin(EditarCitaAdminDTO dto) {
        // Obtener la cita existente
        Cita citaExistente = citasRepository.findById(dto.citaId())
                .orElseThrow(() -> new HistorialException("No se encontró la cita con ID: " + dto.citaId()));

        // Validar la nueva fecha
        if (dto.fechaHora().isBefore(Instant.now())) {
            throw new HistorialException("La fecha y hora de la cita no pueden estar en el pasado.");
        }

        // Validar y obtener paciente
        User paciente = obtenerYValidarUsuario(dto.pacienteId(), Rol.PACIENTE, "Paciente");

        // Validar y obtener odontólogo
        User odontologo = obtenerYValidarUsuario(dto.odontologoId(), Rol.DOCTOR, "Odontólogo");

        if (paciente.getIdNumber().equals(odontologo.getIdNumber())) {
            throw new HistorialException("Un usuario no puede ser paciente y odontólogo en la misma cita.");
        }

        // Actualizar los datos de la cita
        citaExistente.setPaciente(paciente);
        citaExistente.setOdontologo(odontologo);
        citaExistente.setFechaHora(dto.fechaHora());
        citaExistente.setTipoCita(dto.tipoCita());

        return citasRepository.save(citaExistente);
    }

    /**
     * Permite a un paciente editar la fecha y hora de su propia cita.
     * @param dto DTO con la nueva fecha y hora
     * @param userId Identificador del paciente que intenta editar la cita
     * @return La cita actualizada
     * @throws HistorialException si:
     *         - La cita no existe
     *         - El usuario no es el dueño de la cita
     *         - La nueva fecha está en el pasado
     *         - No hay 24 horas de anticipación para el cambio
     */
    @Override
    public Cita editarCitaPaciente(EditarCitaPacienteDTO dto, Long userId) {
        // Obtener la cita existente
        Cita citaExistente = citasRepository.findById(dto.citaId())
                .orElseThrow(() -> new HistorialException("No se encontró la cita con ID: " + dto.citaId()));

        // Verificar que la cita pertenece al paciente
        if (!citaExistente.getPaciente().getIdNumber().equals(userId)) {
            throw new HistorialException("No tienes permiso para editar esta cita.");
        }

        // Validar la nueva fecha
        if (dto.fechaHora().isBefore(Instant.now())) {
            throw new HistorialException("La fecha y hora de la cita no pueden estar en el pasado.");
        }

        // Validar que la nueva fecha sea al menos 24 horas después
        if (dto.fechaHora().isBefore(Instant.now().plusSeconds(24 * 60 * 60))) {
            throw new HistorialException("Las citas deben modificarse con al menos 24 horas de anticipación.");
        }

        // Actualizar solo la fecha y hora de la cita
        citaExistente.setFechaHora(dto.fechaHora());

        return citasRepository.save(citaExistente);
    }
}