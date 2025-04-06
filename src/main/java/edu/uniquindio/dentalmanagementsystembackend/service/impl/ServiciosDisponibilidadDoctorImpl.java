package edu.uniquindio.dentalmanagementsystembackend.service.impl;

import edu.uniquindio.dentalmanagementsystembackend.Enum.EstadoDisponibilidad;
import edu.uniquindio.dentalmanagementsystembackend.entity.DisponibilidadDoctor;
import edu.uniquindio.dentalmanagementsystembackend.entity.Account.User;
import edu.uniquindio.dentalmanagementsystembackend.repository.DisponibilidadDoctorRepository;
import edu.uniquindio.dentalmanagementsystembackend.repository.UserRepository;
import edu.uniquindio.dentalmanagementsystembackend.service.Interfaces.ServiciosDisponibilidadDoctor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class ServiciosDisponibilidadDoctorImpl implements ServiciosDisponibilidadDoctor {

    @Autowired
    private DisponibilidadDoctorRepository disponibilidadDoctorRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public List<LocalDate> obtenerFechasDisponibles(String doctorId, LocalDate fechaInicio, LocalDate fechaFin) {
        System.out.println("\n=== Obteniendo fechas disponibles para el doctor ID: " + doctorId + " ===");
        System.out.println("Rango de fechas: " + fechaInicio + " a " + fechaFin);
        
        try {
            // 1. Verificar que el doctor existe
            User doctor = userRepository.findByIdNumber(doctorId)
                    .orElseThrow(() -> new IllegalArgumentException("Doctor no encontrado con ID: " + doctorId));
            
            System.out.println("Doctor encontrado: " + doctor.getName() + " " + doctor.getLastName());
            
            // 2. Obtener las disponibilidades del doctor
            List<DisponibilidadDoctor> disponibilidades = new ArrayList<>();
            
            try {
                // Obtener disponibilidades para cada día de la semana
                for (DayOfWeek dia : DayOfWeek.values()) {
                    System.out.println("Buscando disponibilidades para el día: " + dia);
                    List<DisponibilidadDoctor> disponibilidadesDia = disponibilidadDoctorRepository
                            .findByDoctor_IdNumberAndDiaSemanaAndEstado(doctorId, dia, EstadoDisponibilidad.ACTIVO);
                    System.out.println("Disponibilidades encontradas para " + dia + ": " + disponibilidadesDia.size());
                    disponibilidades.addAll(disponibilidadesDia);
                }
            } catch (Exception e) {
                System.out.println("Error al buscar disponibilidades: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("Error al buscar disponibilidades: " + e.getMessage());
            }
            
            if (disponibilidades.isEmpty()) {
                System.out.println("El doctor no tiene disponibilidades registradas");
                return new ArrayList<>();
            }
            
            // 3. Generar lista de fechas disponibles
            List<LocalDate> fechasDisponibles = new ArrayList<>();
            LocalDate fechaActual = fechaInicio;
            
            while (!fechaActual.isAfter(fechaFin)) {
                DayOfWeek diaSemana = fechaActual.getDayOfWeek();
                
                // Verificar si el doctor tiene disponibilidad para este día de la semana
                boolean tieneDisponibilidad = disponibilidades.stream()
                        .anyMatch(d -> d.getDiaSemana() == diaSemana);
                
                if (tieneDisponibilidad) {
                    fechasDisponibles.add(fechaActual);
                }
                
                fechaActual = fechaActual.plusDays(1);
            }
            
            System.out.println("Se encontraron " + fechasDisponibles.size() + " fechas disponibles");
            fechasDisponibles.forEach(fecha -> System.out.println("- " + fecha));
            
            return fechasDisponibles;
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al obtener las fechas disponibles: " + e.getMessage());
        }
    }
    
    @Override
    public List<LocalTime> obtenerHorariosDisponibles(String doctorId, LocalDate fecha) {
        System.out.println("\n=== Obteniendo horarios disponibles para el doctor ID: " + doctorId + " ===");
        System.out.println("Fecha: " + fecha);
        
        try {
            // 1. Verificar que el doctor existe
            User doctor = userRepository.findByIdNumber(doctorId)
                    .orElseThrow(() -> new IllegalArgumentException("Doctor no encontrado con ID: " + doctorId));
            
            System.out.println("Doctor encontrado: " + doctor.getName() + " " + doctor.getLastName());
            
            // 2. Obtener la disponibilidad para el día de la semana
            DayOfWeek diaSemana = fecha.getDayOfWeek();
            List<DisponibilidadDoctor> disponibilidades = disponibilidadDoctorRepository
                    .findByDoctor_IdNumberAndDiaSemanaAndEstado(doctorId, diaSemana, EstadoDisponibilidad.ACTIVO);
            
            if (disponibilidades.isEmpty()) {
                System.out.println("El doctor no tiene disponibilidad para el día " + diaSemana);
                return new ArrayList<>();
            }
            
            DisponibilidadDoctor disponibilidad = disponibilidades.get(0);
            
            // 3. Generar lista de horarios disponibles
            List<LocalTime> horariosDisponibles = new ArrayList<>();
            LocalTime horaActual = disponibilidad.getHoraInicio();
            LocalTime horaFin = disponibilidad.getHoraFin();
            
            while (horaActual.isBefore(horaFin)) {
                horariosDisponibles.add(horaActual);
                horaActual = horaActual.plusMinutes(30); // Intervalos de 30 minutos
            }
            
            System.out.println("Se encontraron " + horariosDisponibles.size() + " horarios disponibles");
            horariosDisponibles.forEach(hora -> System.out.println("- " + hora));
            
            return horariosDisponibles;
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al obtener los horarios disponibles: " + e.getMessage());
        }
    }
} 