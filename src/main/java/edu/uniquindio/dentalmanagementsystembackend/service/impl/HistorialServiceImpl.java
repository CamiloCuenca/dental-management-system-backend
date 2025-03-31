package edu.uniquindio.dentalmanagementsystembackend.service.impl;

import edu.uniquindio.dentalmanagementsystembackend.entity.Account.User;
import edu.uniquindio.dentalmanagementsystembackend.entity.HistorialMedico;
import edu.uniquindio.dentalmanagementsystembackend.repository.HistorialMedicoRepository;
import edu.uniquindio.dentalmanagementsystembackend.repository.UserRepository;
import edu.uniquindio.dentalmanagementsystembackend.service.Interfaces.HistorialService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HistorialServiceImpl implements HistorialService {
    
    private final HistorialMedicoRepository historialRepository;
    private final UserRepository userRepository;

    @Override
    public HistorialMedico crearHistorial(Long pacienteId, Long odontologoId, LocalDate fecha, 
                                        String diagnostico, String tratamiento, String observaciones, 
                                        LocalDate proximaCita) {
        User paciente = userRepository.findById(pacienteId).orElseThrow();
        User odontologo = userRepository.findById(odontologoId).orElseThrow();
        return historialRepository.save(new HistorialMedico(paciente, odontologo, fecha, 
                                                          diagnostico, tratamiento, observaciones, 
                                                          proximaCita));
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
