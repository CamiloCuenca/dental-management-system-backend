package edu.uniquindio.dentalmanagementsystembackend.service.impl;

import edu.uniquindio.dentalmanagementsystembackend.Enum.EstadoCitas;
import edu.uniquindio.dentalmanagementsystembackend.Enum.Rol;
import edu.uniquindio.dentalmanagementsystembackend.dto.historial.CrearHistorialDTO;
import edu.uniquindio.dentalmanagementsystembackend.entity.Account.User;
import edu.uniquindio.dentalmanagementsystembackend.entity.Cita;
import edu.uniquindio.dentalmanagementsystembackend.entity.HistorialMedico;
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
        try {
            // Obtener y validar paciente
            User paciente = userRepository.findById(dto.pacienteId())
                    .orElseThrow(() -> new HistorialException("Paciente no encontrado con ID: " + dto.pacienteId()));
            
            if (paciente.getAccount() == null || paciente.getAccount().getRol() != Rol.PACIENTE) {
                throw new HistorialException("El usuario con ID " + dto.pacienteId() + " no es un paciente.");
            }

            // Obtener y validar odontólogo
            User odontologo = userRepository.findById(dto.odontologoId())
                    .orElseThrow(() -> new HistorialException("Odontólogo no encontrado con ID: " + dto.odontologoId()));
            
            if (odontologo.getAccount() == null || odontologo.getAccount().getRol() != Rol.DOCTOR) {
                throw new HistorialException("El usuario con ID " + dto.odontologoId() + " no es un odontólogo.");
            }

            // Obtener y validar cita
            Cita cita = citasRepository.findById(dto.citaId())
                    .orElseThrow(() -> new HistorialException("Cita no encontrada con ID: " + dto.citaId()));

            // Validar que la cita corresponda al paciente y odontólogo
            if (!cita.getPaciente().getIdNumber().equals(paciente.getIdNumber())) {
                throw new HistorialException("La cita no corresponde al paciente especificado.");
            }
            if (!cita.getOdontologo().getIdNumber().equals(odontologo.getIdNumber())) {
                throw new HistorialException("La cita no corresponde al odontólogo especificado.");
            }

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

            // Establecer las relaciones bidireccionales
            paciente.agregarHistorialComoPaciente(historial);
            odontologo.agregarHistorialComoOdontologo(historial);

            // Guardar el historial
            HistorialMedico historialGuardado = historialRepository.save(historial);
            System.out.println("Historial guardado con ID: " + historialGuardado.getId());
            return historialGuardado;
        } catch (HistorialException e) {
            throw e;
        } catch (Exception e) {
            throw new HistorialException("Error al crear el historial: " + e.getMessage());
        }
    }

    @Override
    public List<HistorialMedico> obtenerHistorialPorPaciente(Long pacienteId) {
        return historialRepository.findByPacienteIdNumber(pacienteId.toString());
    }

    @Override
    public List<HistorialMedico> obtenerTodosLosHistoriales() {
        return historialRepository.findAll();
    }
}
