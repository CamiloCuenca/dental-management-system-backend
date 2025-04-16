package edu.uniquindio.dentalmanagementsystembackend.service.impl;

import edu.uniquindio.dentalmanagementsystembackend.Enum.EstadoCitas;
import edu.uniquindio.dentalmanagementsystembackend.Enum.Rol;
import edu.uniquindio.dentalmanagementsystembackend.dto.historial.ActualizarHistorial;
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
import java.util.Map;
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
        User doctor = obtenerYValidarUsuario(dto.odontologoId(), Rol.DOCTOR, "Odontólogo");

        // Obtener y validar cita
        Cita cita = citasRepository.findById(dto.citaId())
                .orElseThrow(() -> new HistorialException("Cita no encontrada con ID: " + dto.citaId()));

        // Validar que la cita corresponda al paciente y odontólogo
        validarCitaConUsuario(cita, paciente, doctor);

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
        historial.setPaciente(paciente);
        historial.setDoctor(doctor);

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
    private User obtenerYValidarUsuario(String usuarioId, Rol rolEsperado, String tipoUsuario) {
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
        if (!cita.getDoctor().getIdNumber().equals(odontologo.getIdNumber())) {
            throw new HistorialException("La cita no corresponde al odontólogo especificado.");
        }
    }

    @Override
    public Map<Integer, List<HistorialDTO>> listarHistorialesPorPacienteAgrupadosPorAnio(String idPaciente) {
        List<HistorialMedico> historiales = historialRepository.buscarHistorialesPorIdPaciente(idPaciente);

        return historiales.stream()
                .map(this::convertirADTO)
                .collect(Collectors.groupingBy(h -> h.fecha().getYear()));
    }

    @Override
    public void ActualizarHistorial(Long id, ActualizarHistorial nuevoHistorial) {
        // Validar que el historial existe
        HistorialMedico historial = historialRepository.findById(id)
                .orElseThrow(() -> new HistorialException("Historial no encontrado con ID: " + id));

        //Acttualizar datos del historial
        historial.setDiagnostico(nuevoHistorial.diagnostico());
        historial.setTratamiento(nuevoHistorial.tratamiento());
        historial.setObservaciones(nuevoHistorial.observaciones());

        // Guardar los cambios en el historial
        historialRepository.save(historial);
    }

    @Override
    public void eliminarHistorial(Long id) {
        // Validar que el historial existe
        HistorialMedico historial = historialRepository.findById(id)
                .orElseThrow(() -> new HistorialException("Historial no encontrado con ID: " + id));
        // TODO: Analizar si se puede eliminar el historial o agregar a la BD un nuevo campo para representar la supuesta eliminacion del historial
    }

    private HistorialDTO convertirADTO(HistorialMedico historial) {
        return new HistorialDTO(
                historial.getId(),
                historial.getPaciente().getName()+historial.getPaciente().getLastName(),
                historial.getDoctor().getName()+historial.getDoctor().getLastName(),
                historial.getFecha(),
                historial.getDiagnostico(),
                historial.getTratamiento(),
                historial.getObservaciones(),
                historial.getCita().getTipoCita().getNombre()
        );
    }



}
