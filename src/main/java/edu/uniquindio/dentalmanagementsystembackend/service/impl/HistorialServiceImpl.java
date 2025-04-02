package edu.uniquindio.dentalmanagementsystembackend.service.impl;

import edu.uniquindio.dentalmanagementsystembackend.Enum.EstadoCitas;
import edu.uniquindio.dentalmanagementsystembackend.Enum.Rol;
import edu.uniquindio.dentalmanagementsystembackend.dto.historial.CrearHistorialDTO;
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

@Service
@RequiredArgsConstructor
@Transactional
public class HistorialServiceImpl implements HistorialService {
    
    private final HistorialMedicoRepository historialRepository;
    private final UserRepository userRepository;
    private final CitasRepository citasRepository;

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

        // Establecer relaciones bidireccionales
        paciente.agregarHistorialComoPaciente(historial);
        odontologo.agregarHistorialComoOdontologo(historial);

        // Guardar el historial
        return historialRepository.save(historial);
    }


    private User obtenerYValidarUsuario(Long usuarioId, Rol rolEsperado, String tipoUsuario) {
        User usuario = userRepository.findById(usuarioId)
                .orElseThrow(() -> new HistorialException(tipoUsuario + " no encontrado con ID: " + usuarioId));

        if (usuario.getAccount() == null || usuario.getAccount().getRol() != rolEsperado) {
            throw new HistorialException("El usuario con ID " + usuarioId + " no es un " + tipoUsuario.toLowerCase() + ".");
        }
        return usuario;
    }

    private void validarCitaConUsuario(Cita cita, User paciente, User odontologo) {
        if (!cita.getPaciente().getIdNumber().equals(paciente.getIdNumber())) {
            throw new HistorialException("La cita no corresponde al paciente especificado.");
        }
        if (!cita.getOdontologo().getIdNumber().equals(odontologo.getIdNumber())) {
            throw new HistorialException("La cita no corresponde al odontólogo especificado.");
        }
    }

    @Override
    public List<HistorialMedico> obtenerHistorialPorPaciente(String pacienteId) {
        return List.of();
    }

    @Override
    public List<HistorialMedico> obtenerTodosLosHistoriales() {
        return historialRepository.findAll();
    }
}
