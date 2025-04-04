package edu.uniquindio.dentalmanagementsystembackend.service.impl;

import edu.uniquindio.dentalmanagementsystembackend.Enum.EstadoCitas;
import edu.uniquindio.dentalmanagementsystembackend.Enum.Rol;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.CrearCitaDTO;
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
}