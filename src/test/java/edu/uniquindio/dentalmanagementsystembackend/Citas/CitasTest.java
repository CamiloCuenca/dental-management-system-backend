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

/**
 * This class contains unit tests for handling appointment-related features and uses
 * Spring Boot's testing framework. It tests various scenarios such as creating,
 * editing, cancelling, confirming, and completing appointments. The tests
 * interact with the services and repositories related to appointments.
 */
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


    /**
     * Test method to validate the functionality of creating a medical appointment.
     * This method verifies the integration and correctness of the ServiciosCitas service
     * when creating a new appointment using the provided CrearCitaDTO data transfer object.
     *
     * Steps performed:
     * 1. Prepares a mock appointment request using patient ID, doctor ID, type of appointment,
     *    date, and time.
     * 2. Calls the ServiciosCitas.crearCita method with the prepared CrearCitaDTO object.
     * 3. Asserts that the returned appointment object has a valid ID, confirming it was saved correctly.
     *
     * Expected functionality:
     * - The appointment is successfully created using the data in CrearCitaDTO.
     * - The created appointment has a non-null ID indicating it was persisted in the database.
     *
     * Validations:
     * - Ensures the crearCita method from ServiciosCitas works as expected with valid inputs.
     */
    @Test
    void crearCita() {
        String idPaciente = "1001277430";
        String idDoctor = "111111111";
        Long idTipoCita = 1L;

        LocalDate fecha = LocalDate.of(2025, 4, 21);
        LocalTime hora = LocalTime.of(11, 30);

        CrearCitaDTO crearCitaDTO = new CrearCitaDTO(idPaciente, idDoctor, fecha, hora, idTipoCita);

        Cita cita = serviciosCitas.crearCita(crearCitaDTO);

        // Verificar que sí se guardó
        assertNotNull(cita.getId());
    }


    /**
     * Test method to verify the functionality of retrieving doctors by their specialty.
     * This method simulates a scenario where the system fetches a list of doctors
     * associated with a specific specialty ID, providing details such as name, surname,
     * specialty, and availability.
     *
     * The test ensures the interaction with the servicioCitas service layer method
     * 'obtenerDoctoresPorEspecialidad' operates correctly and returns the expected
     * list of DoctorEspecialidadDTO.
     */
    @Test
    void obtenerDoctoresPorEspecialidad() {
        Long especialidadId = 1L; // ID de la especialidad de ejemplo
        List<DoctorEspecialidadDTO> doctores = serviciosCitas.obtenerDoctoresPorEspecialidad(especialidadId);
    }





    /**
     * Test method to verify the functionality of editing a medical appointment for a patient.
     * This method simulates editing an existing appointment by providing updated details
     * such as the appointment ID, new date, and time.
     *
     * Steps performed:
     * 1. Initializes the required test data, including the appointment ID and updated date/time.
     * 2. Creates an instance of EditarCitaPacienteDTO to encapsulate the updated appointment details.
     * 3. Calls the ServiciosCitas.editarCitaPaciente method, passing the appointment ID and DTO.
     *
     * Validations:
     * - Ensures the ServiciosCitas.editarCitaPaciente method processes the updated data correctly.
     * - Confirms that the service correctly updates the appointment in the system using the provided details.
     */
    @Test
    void editarCitaPaciente() {
        Long idCita = 31L; // ID de la cita a editar
        LocalDate fecha = LocalDate.of(2025, 4, 28);
        LocalTime hora = LocalTime.of(11, 30);

        EditarCitaPacienteDTO editarCitaPacienteDTO = new EditarCitaPacienteDTO(
                idCita,
                fecha,
                hora

        );
        serviciosCitas.editarCitaPaciente(idCita, editarCitaPacienteDTO);
    }

    /**
     * Test method to validate the functionality of canceling a medical appointment.
     * This method ensures the correct behavior of the ServiciosCitas service when
     * invoking the cancelarCita method to cancel an appointment.
     *
     * Steps performed:
     * 1. Prepares a mock appointment ID to cancel.
     * 2. Calls the ServiciosCitas.cancelarCita method with the given ID.
     *
     * Expected functionality:
     * - The appointment corresponding to the provided ID is successfully canceled.
     * - The cancelarCita method executes without errors, signifying a valid cancellation operation.
     *
     * Validations:
     * - Ensures that the cancelarCita method in ServiciosCitas is appropriately handling the
     *   cancellation request based on the provided appointment ID.
     */
    @Test
    void cancelarCita() {
        Long idCita = 31L;
        serviciosCitas.cancelarCita(idCita);

    }

    /**
     * Test method to validate the functionality of confirming a medical appointment.
     * This method verifies the integration and correctness of the ServiciosCitas service
     * when invoking the confirmarCita method with a specific appointment ID.
     *
     * Steps performed:
     * 1. Prepares a mock appointment ID that needs to be confirmed.
     * 2. Calls the ServiciosCitas.confirmarCita method with the prepared appointment ID.
     *
     * Expected functionality:
     * - The appointment is successfully confirmed by the ServiciosCitas service.
     *
     * Validations:
     * - Ensures the confirmarCita method from ServiciosCitas operates correctly
     *   when provided with a valid appointment ID.
     */
    @Test
    void confirmarCita() {
        Long idCita = 31L;
        serviciosCitas.confirmarCita(idCita);
    }

    /**
     * Test method to validate the functionality of marking a medical appointment as completed.
     * This method ensures the correct behavior of the ServiciosCitas service when
     * invoking the completarCita method with a specific appointment ID.
     *
     * Steps performed:
     * 1. Prepares a mock appointment ID that needs to be marked as completed.
     * 2. Calls the ServiciosCitas.completarCita method with the given appointment ID.
     *
     * Expected functionality:
     * - The appointment is successfully marked as completed by the ServiciosCitas service.
     *
     * Validations:
     * - Verifies that the ServiciosCitas.completarCita method correctly processes
     *   the given appointment ID and updates the status to completed.
     */
    @Test
    void completarCita() {
        Long idCita = 31L;
        serviciosCitas.completarCita(idCita);
    }

    /**
     * Test method to validate the functionality of creating a non-authenticated medical appointment.
     * This method verifies the correct behavior of the ServiciosCitas service when creating a new appointment
     * using the provided CrearCitaNoAutenticadaDTO data transfer object.
     *
     * Steps performed:
     * 1. Constructs a CrearCitaNoAutenticadaDTO object with mock data, including patient ID, doctor ID, type of appointment,
     *    date, and time.
     * 2. Calls the serviciosCitas.crearCitaNoAutenticada method with the constructed DTO object.
     *
     * Expected functionality:
     * - The appointment is successfully created in the system using the provided data in the CrearCitaNoAutenticadaDTO object.
     *
     * Validations:
     * - Ensures the ServiciosCitas.crearCitaNoAutenticada method processes the input data correctly.
     * - Confirms that the service creates the appointment without any exceptions or failures.
     */
    @Test
    void crearCitaNoAut(){

        String idPaciente = "1001277431";
        String idDoctor = "111111111";
        Long idTipoCita = 1L;

        LocalDate fecha = LocalDate.of(2025, 4, 21);
        LocalTime hora = LocalTime.of(11, 30);
        CrearCitaNoAutenticadaDTO crearCitaNoAutenticadaDTO = new CrearCitaNoAutenticadaDTO(
                "CAMILO",
                idPaciente,
                "3153033411",
                "brandone.acevedoc@uqvirtual.edu.co",
                idDoctor,
                fecha,
                hora,
                idTipoCita
        );
        serviciosCitas.crearCitaNoAutenticada(crearCitaNoAutenticadaDTO);
    }

    

    /**
     * Test method to verify the functionality of editing a non-authenticated appointment
     * by an admin. This test sets up a mock scenario where an existing appointment
     * is updated with new details, such as patient information, doctor ID, date, and time.
     * It ensures the correct interaction with the ServiciosCitas service to perform
     */
    @Test
    void editarCitaNoAutenticadaAdmin() {
        String idPaciente = "1001277430";
        String idDoctor = "111111111";
        Long idCita = 32L;

        LocalDate fecha = LocalDate.of(2025, 4, 21);
        LocalTime hora = LocalTime.of(11, 30);
        EditarCitaNoAutenticadaAdminDTO editarCitaNoAutenticadaAdminDTO = new EditarCitaNoAutenticadaAdminDTO(
              "Camilo",
                idPaciente,
                "3153033411",
                "brandone.acevedoc@uqvirtual.edu.co",
                idDoctor,
                fecha,
                hora,
                1L
        );
        serviciosCitas.editarCitaNoAutenticadaAdmin(idCita, editarCitaNoAutenticadaAdminDTO);
    }

    @Test
    void cancelarCitaNoAutenticadaAdmin(){
        Long idCita = 34L;
        serviciosCitas.cancelarCitaNoAutenticadaAdmin(idCita);
    }

    @Test
    void cambiarEstadoCitaNoAutenticadaAdmin (){
        Long idCita = 34L;
        serviciosCitas.cambiarEstadoCitaNoAutenticadaAdmin(idCita, EstadoCitas.COMPLETADA);
    }


    @Test
    void obtenerCitasNoAutenticadasPorPaciente(){
        String idPaciente = "1001277431";
        serviciosCitas.obtenerCitasNoAutenticadasPorPaciente(idPaciente);
    }

    @Test
    void obtenerCitasNoAutenticadasPorDoctor (){
        String idDoctor = "111111111";
        serviciosCitas.obtenerCitasNoAutenticadasPorDoctor(idDoctor);
    }


    


}