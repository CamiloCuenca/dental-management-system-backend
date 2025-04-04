package edu.uniquindio.dentalmanagementsystembackend.service.impl;

import edu.uniquindio.dentalmanagementsystembackend.Enum.EstadoCitas;
import edu.uniquindio.dentalmanagementsystembackend.Enum.Rol;
import edu.uniquindio.dentalmanagementsystembackend.dto.historial.CrearHistorialDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.historial.HistorialDTO;
import edu.uniquindio.dentalmanagementsystembackend.entity.Account.HistorialMedico;
import edu.uniquindio.dentalmanagementsystembackend.entity.Account.User;
import edu.uniquindio.dentalmanagementsystembackend.entity.Cita;
import edu.uniquindio.dentalmanagementsystembackend.exception.HistorialException;
import edu.uniquindio.dentalmanagementsystembackend.repository.CitasRepository;
import edu.uniquindio.dentalmanagementsystembackend.repository.HistorialMedicoRepository;
import edu.uniquindio.dentalmanagementsystembackend.repository.UserRepository;
import edu.uniquindio.dentalmanagementsystembackend.service.Interfaces.HistorialService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de gestión de historiales médicos.
 * Esta clase maneja toda la lógica de negocio relacionada con los historiales médicos,
 * incluyendo su creación, consulta y validación.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class HistorialServiceImpl implements HistorialService {
    
    private final HistorialMedicoRepository historialRepository;
    private final UserRepository userRepository;
    private final CitasRepository citasRepository;

    /**
     * Crea un nuevo registro en el historial médico.
     * Realiza validaciones de seguridad y consistencia de datos antes de crear el historial.
     *
     * @param dto DTO con la información del historial médico
     * @return HistorialMedico creado
     * @throws HistorialException Si hay algún error en la validación o creación
     */
    @Override
    @Transactional
    public HistorialMedico crearHistorial(CrearHistorialDTO dto) {

        // Obtener y validar paciente
        User paciente = obtenerYValidarUsuario(dto.pacienteId(), Rol.PACIENTE, "Paciente");

        // Obtener y validar odontólogo
        User odontologo = obtenerYValidarUsuario(dto.odontologoId(), Rol.DOCTOR, "Odontólogo");

        // Obtener y validar cita
        Cita cita = citasRepository.findById(dto.citaId())
                .orElseThrow(() -> new HistorialException("Cita no encontrada con ID: " + dto.citaId()));

        // Validar que la cita corresponda al paciente y odontólogo
        validarCitaConUsuario(cita, paciente, odontologo);

        // Validar estado de la cita
        if (cita.getEstado() != EstadoCitas.CONFIRMADA) {
            throw new HistorialException("Solo se pueden crear historiales para citas confirmadas.");
        }

        // Validar fecha del historial
        if (dto.fecha().isAfter(LocalDate.now())) {
            throw new HistorialException("La fecha del historial no puede ser futura.");
        }

        // Crear el historial
        HistorialMedico historial = new HistorialMedico();
        historial.setFecha(dto.fecha());
        historial.setDiagnostico(dto.diagnostico());
        historial.setTratamiento(dto.tratamiento());
        historial.setObservaciones(dto.observaciones());
        historial.setProximaCita(dto.proximaCita());
        historial.setCita(cita);

        // Guardar el historial
        return historialRepository.save(historial);
    }

    /**
     * Obtiene y valida que un usuario exista y tenga el rol correcto.
     *
     * @param usuarioId ID del usuario a validar
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
     * Valida que una cita corresponda al paciente y odontólogo especificados.
     *
     * @param cita Cita a validar
     * @param paciente Paciente esperado
     * @param odontologo Odontólogo esperado
     * @throws HistorialException Si la cita no corresponde a los usuarios especificados
     */
    private void validarCitaConUsuario(Cita cita, User paciente, User odontologo) {
        if (!cita.getPaciente().getIdNumber().equals(paciente.getIdNumber())) {
            throw new HistorialException("La cita no corresponde al paciente especificado.");
        }
        if (!cita.getOdontologo().getIdNumber().equals(odontologo.getIdNumber())) {
            throw new HistorialException("La cita no corresponde al odontólogo especificado.");
        }
    }


}
