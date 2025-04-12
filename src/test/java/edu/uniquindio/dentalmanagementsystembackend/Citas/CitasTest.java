package edu.uniquindio.dentalmanagementsystembackend.Citas;

// Importa la enumeración TipoCita desde el paquete Enum

import edu.uniquindio.dentalmanagementsystembackend.Enum.EstadoCitas;
import edu.uniquindio.dentalmanagementsystembackend.Enum.Rol;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.*;
import edu.uniquindio.dentalmanagementsystembackend.entity.Account.Account;
import edu.uniquindio.dentalmanagementsystembackend.entity.Account.User;
import edu.uniquindio.dentalmanagementsystembackend.entity.Cita;
import edu.uniquindio.dentalmanagementsystembackend.entity.DisponibilidadDoctor;
import edu.uniquindio.dentalmanagementsystembackend.entity.Especialidad;
import edu.uniquindio.dentalmanagementsystembackend.entity.TipoCita;
import edu.uniquindio.dentalmanagementsystembackend.repository.CitasRepository;
import edu.uniquindio.dentalmanagementsystembackend.repository.CuentaRepository;
import edu.uniquindio.dentalmanagementsystembackend.repository.DisponibilidadDoctorRepository;
import edu.uniquindio.dentalmanagementsystembackend.repository.EspecialidadRepository;
import edu.uniquindio.dentalmanagementsystembackend.repository.TipoCitaRepository;
import edu.uniquindio.dentalmanagementsystembackend.repository.UserRepository;
// Importa la clase ListaCitasDTO desde el paquete dto
// Importa la interfaz CitasRepository desde el paquete repository
// Importa la interfaz ServiciosCitas desde el paquete service. Interfaces
import edu.uniquindio.dentalmanagementsystembackend.service.Interfaces.ServiciosCitas;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import edu.uniquindio.dentalmanagementsystembackend.util.DateUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

// Anotación que indica que esta clase es una prueba de Spring Boot
@SpringBootTest
public class CitasTest {

    // Inyección de dependencias para el repositorio de citas
    @Autowired
    private CitasRepository citasRepository;

    // Inyección de dependencias para el servicio de citas
    @Autowired
    private ServiciosCitas serviciosCitas;

    // Inyección de dependencias para otros repositorios necesarios
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CuentaRepository cuentaRepository;

    @Autowired
    private TipoCitaRepository tipoCitaRepository;

    @Autowired
    private DisponibilidadDoctorRepository disponibilidadDoctorRepository;

    @Autowired
    private EspecialidadRepository especialidadRepository;

    @PersistenceContext
    private EntityManager entityManager;


    @Test
    void crearCita() {
        String idPaciente = "1001277430";
        String idDoctor = "111111111";
        Long idTipoCita = 1L;

        LocalDate fecha = LocalDate.of(2025, 4, 14);
        LocalTime hora = LocalTime.of(11, 30);

        CrearCitaDTO crearCitaDTO = new CrearCitaDTO(idPaciente, idDoctor, fecha, hora, idTipoCita);

        Cita cita = serviciosCitas.crearCita(crearCitaDTO);

        // Verificar que sí se guardó
        assertNotNull(cita.getId());
    }


    @Test
    void obtenerDoctoresPorEspecialidad() {
        Long especialidadId = 1L; // ID de la especialidad de ejemplo
        List<DoctorEspecialidadDTO> doctores = serviciosCitas.obtenerDoctoresPorEspecialidad(especialidadId);
    }


    @Test
    void obtenerCitasPorPaciente() {
       String idPaciente = "1001277430"; // ID del paciente de ejemplo
        List<CitaDTO> citas = serviciosCitas.obtenerCitasPorPaciente(idPaciente);
        assertNotNull(citas);
        assertFalse(citas.isEmpty());
    }

    @Test
    void obtenerCitasPorDoctor() {
        String idDoctor = "111111111"; // ID del doctor de ejemplo
        List<CitaDTO> citas = serviciosCitas.obtenerCitasPorDoctor(idDoctor);
    }

    @Test
    void editarCitaAdmin() {
       Long idCita = 28L; // ID de la cita a editar
        EditarCitaAdminDTO editarCitaAdminDTO = new EditarCitaAdminDTO(
                idCita,
                "1001277430", // ID del paciente
                "111111111", // ID del doctor
                DateUtil.crearFechaHoraBogota(2025, 4, 14, 11, 30) // Nueva fecha y hora de la cita
        );


        Cita citaEditada = serviciosCitas.editarCitaAdmin(idCita, editarCitaAdminDTO);

    }

    @Test
    void editarCitaPaciente() {
        Long idCita = 24L; // ID de la cita a editar
        LocalDate fecha = LocalDate.of(2025, 4, 14);
        LocalTime hora = LocalTime.of(11, 30);

        EditarCitaPacienteDTO editarCitaPacienteDTO = new EditarCitaPacienteDTO(
                idCita,
                fecha,
                hora

        );
        serviciosCitas.editarCitaPaciente(idCita, editarCitaPacienteDTO);
    }

    @Test
    void cancelarCita() {
        Long idCita = 28L;
        serviciosCitas.cancelarCita(idCita);

    }

    @Test
    void confirmarCita() {
        Long idCita = 28L;
        serviciosCitas.confirmarCita(idCita);
    }

    @Test
    void completarCita() {
        Long idCita = 28L;
        serviciosCitas.completarCita(idCita);
    }


}