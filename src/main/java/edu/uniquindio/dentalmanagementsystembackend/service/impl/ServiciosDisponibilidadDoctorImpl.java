package edu.uniquindio.dentalmanagementsystembackend.service.impl;

import edu.uniquindio.dentalmanagementsystembackend.entity.DisponibilidadDoctor;
import edu.uniquindio.dentalmanagementsystembackend.entity.Account.User;
import edu.uniquindio.dentalmanagementsystembackend.repository.DisponibilidadDoctorRepository;
import edu.uniquindio.dentalmanagementsystembackend.repository.UserRepository;
import edu.uniquindio.dentalmanagementsystembackend.service.Interfaces.ServiciosDisponibilidadDoctor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ServiciosDisponibilidadDoctorImpl implements ServiciosDisponibilidadDoctor {

    @Autowired
    private DisponibilidadDoctorRepository disponibilidadDoctorRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Override
    public List<DisponibilidadDoctor> listarDisponibilidades() {
        return disponibilidadDoctorRepository.findAll();
    }

    @Override
    public DisponibilidadDoctor obtenerDisponibilidadPorId(Long id) {
        return disponibilidadDoctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Disponibilidad no encontrada con ID: " + id));
    }

    @Override
    public List<DisponibilidadDoctor> obtenerDisponibilidadesPorDoctor(Long doctorId) {
        // Verificar que el doctor existe
        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor no encontrado con ID: " + doctorId));
        
        return disponibilidadDoctorRepository.findAll().stream()
                .filter(disp -> disp.getDoctor().getIdNumber().equals(doctor.getIdNumber()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean verificarDisponibilidad(Long doctorId, DayOfWeek diaSemana, LocalTime hora) {
        // Verificar que el doctor existe
        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor no encontrado con ID: " + doctorId));
        
        return disponibilidadDoctorRepository.findAll().stream()
                .anyMatch(disp -> disp.getDoctor().getIdNumber().equals(doctor.getIdNumber()) &&
                        disp.getDiaSemana() == diaSemana &&
                        !hora.isBefore(disp.getHoraInicio()) &&
                        !hora.isAfter(disp.getHoraFin()));
    }

    @Override
    public DisponibilidadDoctor crearDisponibilidad(DisponibilidadDoctor disponibilidad) {
        // Validar que el doctor existe
        if (disponibilidad.getDoctor() == null || disponibilidad.getDoctor().getIdNumber() == null) {
            throw new RuntimeException("El doctor no puede ser nulo");
        }
        
        // Validar que el día de la semana no sea nulo
        if (disponibilidad.getDiaSemana() == null) {
            throw new RuntimeException("El día de la semana no puede ser nulo");
        }
        
        // Validar que las horas no sean nulas
        if (disponibilidad.getHoraInicio() == null || disponibilidad.getHoraFin() == null) {
            throw new RuntimeException("Las horas de inicio y fin no pueden ser nulas");
        }
        
        // Validar que la hora de inicio sea anterior a la hora de fin
        if (disponibilidad.getHoraInicio().isAfter(disponibilidad.getHoraFin())) {
            throw new RuntimeException("La hora de inicio debe ser anterior a la hora de fin");
        }
        
        return disponibilidadDoctorRepository.save(disponibilidad);
    }

    @Override
    public DisponibilidadDoctor actualizarDisponibilidad(DisponibilidadDoctor disponibilidad) {
        // Verificar que la disponibilidad existe
        if (!disponibilidadDoctorRepository.existsById(disponibilidad.getId())) {
            throw new RuntimeException("Disponibilidad no encontrada con ID: " + disponibilidad.getId());
        }
        
        // Validar que el doctor existe
        if (disponibilidad.getDoctor() == null || disponibilidad.getDoctor().getIdNumber() == null) {
            throw new RuntimeException("El doctor no puede ser nulo");
        }
        
        // Validar que el día de la semana no sea nulo
        if (disponibilidad.getDiaSemana() == null) {
            throw new RuntimeException("El día de la semana no puede ser nulo");
        }
        
        // Validar que las horas no sean nulas
        if (disponibilidad.getHoraInicio() == null || disponibilidad.getHoraFin() == null) {
            throw new RuntimeException("Las horas de inicio y fin no pueden ser nulas");
        }
        
        // Validar que la hora de inicio sea anterior a la hora de fin
        if (disponibilidad.getHoraInicio().isAfter(disponibilidad.getHoraFin())) {
            throw new RuntimeException("La hora de inicio debe ser anterior a la hora de fin");
        }
        
        return disponibilidadDoctorRepository.save(disponibilidad);
    }

    @Override
    public void eliminarDisponibilidad(Long id) {
        // Verificar que la disponibilidad existe
        if (!disponibilidadDoctorRepository.existsById(id)) {
            throw new RuntimeException("Disponibilidad no encontrada con ID: " + id);
        }
        
        disponibilidadDoctorRepository.deleteById(id);
    }
} 