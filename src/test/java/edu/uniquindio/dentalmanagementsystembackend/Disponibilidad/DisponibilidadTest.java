package edu.uniquindio.dentalmanagementsystembackend.Disponibilidad;

import edu.uniquindio.dentalmanagementsystembackend.Enum.EstadoDisponibilidad;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.FechaDisponibleDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.HorarioDisponibleDTO;
import edu.uniquindio.dentalmanagementsystembackend.entity.Account.User;
import edu.uniquindio.dentalmanagementsystembackend.entity.DisponibilidadDoctor;
import edu.uniquindio.dentalmanagementsystembackend.repository.DisponibilidadDoctorRepository;
import edu.uniquindio.dentalmanagementsystembackend.repository.UserRepository;
import edu.uniquindio.dentalmanagementsystembackend.service.impl.ServiciosDisponibilidadDoctorImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DisponibilidadTest {

    @Mock
    private DisponibilidadDoctorRepository disponibilidadDoctorRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private ServiciosDisponibilidadDoctorImpl serviciosDisponibilidadDoctor;

    private User testDoctor;
    private DisponibilidadDoctor testDisponibilidad;

    @BeforeEach
    void setUp() {
        testDoctor = new User();
        testDoctor.setIdNumber("111111111");
        testDoctor.setName("María");
        testDoctor.setLastName("García");

        testDisponibilidad = new DisponibilidadDoctor();
        testDisponibilidad.setDoctor(testDoctor);
        testDisponibilidad.setDiaSemana(DayOfWeek.MONDAY);
        testDisponibilidad.setHoraInicio(LocalTime.of(8, 0));
        testDisponibilidad.setHoraFin(LocalTime.of(12, 0));
        testDisponibilidad.setEstado(EstadoDisponibilidad.ACTIVO);
    }

    @Test
    void obtenerFechasDisponibles() {
        LocalDate inicio = LocalDate.now();
        LocalDate fin = inicio.plusDays(7);
        String doctorId = "111111111";

        when(userRepository.findByIdNumber(doctorId)).thenReturn(Optional.of(testDoctor));
        when(disponibilidadDoctorRepository.findByDoctor_IdNumberAndDiaSemanaAndEstado(eq(doctorId), eq(DayOfWeek.MONDAY), eq(EstadoDisponibilidad.ACTIVO)))
                .thenReturn(List.of(testDisponibilidad));
        // Para los otros días, devolver lista vacía
        for (DayOfWeek dia : DayOfWeek.values()) {
            if (dia != DayOfWeek.MONDAY) {
                when(disponibilidadDoctorRepository.findByDoctor_IdNumberAndDiaSemanaAndEstado(eq(doctorId), eq(dia), eq(EstadoDisponibilidad.ACTIVO)))
                        .thenReturn(List.of());
            }
        }

        List<FechaDisponibleDTO> fechas = serviciosDisponibilidadDoctor.obtenerFechasDisponibles(doctorId, inicio, fin);
        assertNotNull(fechas);
        assertTrue(fechas.size() > 0);
        // Verificar que los horarios de los lunes estén en el rango correcto
        for (FechaDisponibleDTO fecha : fechas) {
            assertEquals(DayOfWeek.MONDAY, fecha.fecha().getDayOfWeek());
            assertFalse(fecha.horarios().isEmpty());
        }
        verify(userRepository).findByIdNumber(doctorId);
    }

    @Test
    void obtenerHorariosDisponibles() {
        String doctorId = "111111111";
        LocalDate fecha = LocalDate.now().with(DayOfWeek.MONDAY);

        when(userRepository.findByIdNumber(doctorId)).thenReturn(Optional.of(testDoctor));
        when(disponibilidadDoctorRepository.findByDoctor_IdNumberAndDiaSemanaAndEstado(eq(doctorId), eq(DayOfWeek.MONDAY), eq(EstadoDisponibilidad.ACTIVO)))
                .thenReturn(List.of(testDisponibilidad));

        List<HorarioDisponibleDTO> horarios = serviciosDisponibilidadDoctor.obtenerHorariosDisponibles(doctorId, fecha);
        assertNotNull(horarios);
        assertTrue(horarios.size() > 0);
        // Verificar que los horarios estén dentro del rango de la disponibilidad
        for (HorarioDisponibleDTO horario : horarios) {
            assertTrue(!horario.hora().isBefore(testDisponibilidad.getHoraInicio()) && horario.hora().isBefore(testDisponibilidad.getHoraFin()));
        }
        verify(userRepository).findByIdNumber(doctorId);
    }
}
