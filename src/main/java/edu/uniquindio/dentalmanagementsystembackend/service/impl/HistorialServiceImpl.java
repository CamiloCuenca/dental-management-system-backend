package edu.uniquindio.dentalmanagementsystembackend.service.impl;

import edu.uniquindio.dentalmanagementsystembackend.Enum.EstadoCitas;
import edu.uniquindio.dentalmanagementsystembackend.Enum.Rol;
import edu.uniquindio.dentalmanagementsystembackend.dto.historial.CrearHistorialDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.historial.HistorialDTO;
import edu.uniquindio.dentalmanagementsystembackend.entity.Account.User;
import edu.uniquindio.dentalmanagementsystembackend.entity.Cita;
import edu.uniquindio.dentalmanagementsystembackend.entity.Account.HistorialMedico;
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

    @Override
    @Transactional(readOnly = true)
    public List<HistorialMedico> obtenerHistorialPorPaciente(Long pacienteId) {
        // Validar que el paciente exista y tenga el rol correcto
        User paciente = userRepository.findById(pacienteId)
                .orElseThrow(() -> new HistorialException("Paciente no encontrado con ID: " + pacienteId));

        if (paciente.getAccount() == null || paciente.getAccount().getRol() != Rol.PACIENTE) {
            throw new HistorialException("El usuario con ID " + pacienteId + " no es un paciente.");
        }

        // Obtener los historiales del paciente
        List<HistorialMedico> historiales = historialRepository.findByPacienteIdNumber(paciente.getIdNumber());

        // Si no hay historiales, lanzar una excepción
        if (historiales.isEmpty()) {
            throw new HistorialException("El paciente no tiene historiales médicos registrados.");
        }

        return historiales;
    }

    @Override
    public List<HistorialMedico> obtenerTodosLosHistoriales() {
        return historialRepository.findAll();
    }

    /**
     * Obtiene el historial médico de un paciente en formato DTO.
     * Los historiales se ordenan por fecha descendente.
     *
     * @param pacienteId ID del paciente
     * @return Lista de DTOs del historial médico
     * @throws HistorialException Si el paciente no existe o no tiene historiales
     */
    @Override
    @Transactional(readOnly = true)
    public List<HistorialDTO> obtenerHistorialesDTOPorPaciente(Long pacienteId) {
        List<HistorialMedico> historiales = obtenerHistorialPorPaciente(pacienteId);
        return historiales.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un historial médico específico por su ID.
     *
     * @param historialId ID del historial médico
     * @return DTO del historial médico
     * @throws HistorialException Si el historial no existe
     */
    @Override
    @Transactional(readOnly = true)
    public HistorialDTO obtenerHistorialPorId(Long historialId) {
        HistorialMedico historial = historialRepository.findById(historialId)
                .orElseThrow(() -> new HistorialException("Historial no encontrado con ID: " + historialId));
        return convertirADTO(historial);
    }

    /**
     * Obtiene todos los historiales médicos de una fecha específica.
     *
     * @param fecha Fecha a buscar
     * @return Lista de DTOs del historial médico
     */
    @Override
    @Transactional(readOnly = true)
    public List<HistorialDTO> obtenerHistorialesPorFecha(LocalDate fecha) {
        List<HistorialMedico> historiales = historialRepository.findByFecha(fecha);
        return historiales.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todos los historiales médicos de un odontólogo.
     *
     * @param odontologoId ID del odontólogo
     * @return Lista de DTOs del historial médico
     * @throws HistorialException Si el odontólogo no existe
     */
    @Override
    @Transactional(readOnly = true)
    public List<HistorialDTO> obtenerHistorialesPorOdontologo(Long odontologoId) {
        User odontologo = obtenerYValidarUsuario(odontologoId, Rol.DOCTOR, "Odontólogo");
        List<HistorialMedico> historiales = historialRepository.findByOdontologoIdNumber(odontologo.getIdNumber());
        return historiales.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * Convierte una entidad HistorialMedico a su representación DTO.
     *
     * @param historial Entidad HistorialMedico a convertir
     * @return DTO con la información del historial
     */
    private HistorialDTO convertirADTO(HistorialMedico historial) {
        return new HistorialDTO(
            historial.getId(),
            historial.getPaciente().getName() + " " + historial.getPaciente().getLastName(),
            historial.getOdontologo().getName() + " " + historial.getOdontologo().getLastName(),
            historial.getFecha(),
            historial.getDiagnostico(),
            historial.getTratamiento(),
            historial.getObservaciones(),
            historial.getProximaCita(),
            historial.getCita().getTipoCita().toString()
        );
    }
}
