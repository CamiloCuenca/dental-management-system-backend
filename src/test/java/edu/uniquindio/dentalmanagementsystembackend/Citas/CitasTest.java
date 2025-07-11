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
import edu.uniquindio.dentalmanagementsystembackend.exception.CitaException;
import edu.uniquindio.dentalmanagementsystembackend.repository.CitasRepository;
import edu.uniquindio.dentalmanagementsystembackend.repository.CuentaRepository;
import edu.uniquindio.dentalmanagementsystembackend.repository.DisponibilidadDoctorRepository;
import edu.uniquindio.dentalmanagementsystembackend.repository.EspecialidadRepository;
import edu.uniquindio.dentalmanagementsystembackend.repository.TipoCitaRepository;
import edu.uniquindio.dentalmanagementsystembackend.repository.UserRepository;
// Importa la clase ListaCitasDTO desde el paquete dto
// Importa la interfaz CitasRepository desde el paquete repository
// Importa la interfaz ServiciosCitas desde el paquete service. Interfaces
import edu.uniquindio.dentalmanagementsystembackend.service.Interfaces.EmailService;
import edu.uniquindio.dentalmanagementsystembackend.service.Interfaces.ServiciosDisponibilidadDoctor;
import edu.uniquindio.dentalmanagementsystembackend.service.impl.ServiciosCitaImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * This class contains unit tests for handling appointment-related features and uses
 * Spring Boot's testing framework. It tests various scenarios such as creating,
 * editing, cancelling, confirming, and completing appointments. The tests
 * interact with the services and repositories related to appointments.
 */
// Anotación que indica que esta clase es una prueba de Spring Boot
@ExtendWith(MockitoExtension.class)
class CitasTest {

    @Mock
    private CitasRepository citasRepository;
    @Mock
    private CuentaRepository cuentaRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private DisponibilidadDoctorRepository disponibilidadDoctorRepository;
    @Mock
    private TipoCitaRepository tipoCitaRepository;
    @Mock
    private EspecialidadRepository especialidadRepository;
    @Mock
    private EmailService emailService;
    @Mock
    private ServiciosDisponibilidadDoctor serviciosDisponibilidadDoctor;

    @InjectMocks
    private ServiciosCitaImpl serviciosCita;

    private User testPaciente;
    private User testDoctor;
    private TipoCita testTipoCita;
    private Cita testCita;
    private Cita testCitaPendiente;
    private Especialidad testEspecialidad;

    @BeforeEach
    void setUp() {
        testPaciente = new User();
        testPaciente.setIdNumber("1001277430");
        testPaciente.setName("Juan");
        testPaciente.setLastName("Pérez");
        Account pacienteAccount = new Account();
        pacienteAccount.setRol(Rol.PACIENTE);
        testPaciente.setAccount(pacienteAccount);

        testDoctor = new User();
        testDoctor.setIdNumber("111111111");
        testDoctor.setName("Dr. María");
        testDoctor.setLastName("García");
        Account doctorAccount = new Account();
        doctorAccount.setRol(Rol.DOCTOR);
        testDoctor.setAccount(doctorAccount);

        testTipoCita = new TipoCita();
        testTipoCita.setId(1L);
        testTipoCita.setNombre("Consulta General");

        testEspecialidad = new Especialidad();
        testEspecialidad.setId(1L);
        testEspecialidad.setNombre("Odontología General");

        testCita = new Cita();
        testCita.setId(10L);
        testCita.setPaciente(testPaciente);
        testCita.setDoctor(testDoctor);
        testCita.setFechaHora(LocalDateTime.of(2025, 4, 21, 11, 30).atZone(ZoneId.systemDefault()).toInstant());
        testCita.setEstado(EstadoCitas.CONFIRMADA);
        testCita.setTipoCita(testTipoCita);
        testCita.setEsAutenticada(true);

        testCitaPendiente = new Cita();
        testCitaPendiente.setId(31L);
        testCitaPendiente.setPaciente(testPaciente);
        testCitaPendiente.setDoctor(testDoctor);
        testCitaPendiente.setFechaHora(LocalDateTime.of(2025, 7, 11, 11, 30).atZone(ZoneId.systemDefault()).toInstant());
        testCitaPendiente.setEstado(EstadoCitas.PENDIENTE);
        testCitaPendiente.setTipoCita(testTipoCita);
        testCitaPendiente.setEsAutenticada(true);
    }

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
    void crearCita_success() throws Exception {
        CrearCitaDTO crearCitaDTO = new CrearCitaDTO(
                "1001277430",
                "111111111",
                LocalDate.now().plusDays(1), // Fecha futura
                LocalTime.of(11, 30),
                1L
        );

        when(userRepository.findByIdNumber("1001277430")).thenReturn(Optional.of(testPaciente));
        when(userRepository.findByIdNumber("111111111")).thenReturn(Optional.of(testDoctor));
        when(serviciosDisponibilidadDoctor.validarDisponibilidadDoctor(anyString(), any(LocalDate.class), any(LocalTime.class))).thenReturn(true);
        when(citasRepository.existsByDoctorAndFechaHora(any(User.class), any())).thenReturn(false);
        when(tipoCitaRepository.findById(1L)).thenReturn(Optional.of(testTipoCita));
        when(citasRepository.save(any(Cita.class))).thenReturn(testCita);
        doNothing().when(emailService).enviarCorreoCita(any());

        Cita cita = serviciosCita.crearCita(crearCitaDTO);
        assertNotNull(cita);
        assertEquals(testCita.getId(), cita.getId());
        verify(citasRepository).save(any(Cita.class));
        verify(emailService).enviarCorreoCita(any());
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
        Long especialidadId = 1L; // ID de la especialidad válido
        
        when(especialidadRepository.findById(especialidadId)).thenReturn(Optional.of(testEspecialidad));
        when(userRepository.findByAccount_Rol(Rol.DOCTOR)).thenReturn(List.of(testDoctor));
        
        List<DoctorEspecialidadDTO> doctores = serviciosCita.obtenerDoctoresPorEspecialidad(especialidadId);
        
        assertNotNull(doctores);
        verify(especialidadRepository).findById(especialidadId);
        verify(userRepository).findByAccount_Rol(Rol.DOCTOR);
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
        LocalDate fecha = LocalDate.now().plusDays(2);
        LocalTime hora = LocalTime.of(11, 30);

        EditarCitaPacienteDTO editarCitaPacienteDTO = new EditarCitaPacienteDTO(
                idCita,
                fecha,
                hora
        );
        
        when(citasRepository.findById(idCita)).thenReturn(Optional.of(testCitaPendiente));
        when(citasRepository.save(any(Cita.class))).thenReturn(testCitaPendiente);
        
        serviciosCita.editarCitaPaciente(idCita, editarCitaPacienteDTO);
        
        verify(citasRepository).findById(idCita);
        verify(citasRepository).save(any(Cita.class));
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
        
        when(citasRepository.findById(idCita)).thenReturn(Optional.of(testCitaPendiente));
        when(citasRepository.save(any(Cita.class))).thenReturn(testCitaPendiente);
        
        serviciosCita.cancelarCita(idCita);
        
        verify(citasRepository).findById(idCita);
        verify(citasRepository).save(any(Cita.class));
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
        
        when(citasRepository.findById(idCita)).thenReturn(Optional.of(testCitaPendiente));
        when(citasRepository.save(any(Cita.class))).thenReturn(testCitaPendiente);
        
        serviciosCita.confirmarCita(idCita);
        
        verify(citasRepository).findById(idCita);
        verify(citasRepository).save(any(Cita.class));
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
        
        // Crear una cita confirmada para completar
        Cita citaConfirmada = new Cita();
        citaConfirmada.setId(31L);
        citaConfirmada.setPaciente(testPaciente);
        citaConfirmada.setDoctor(testDoctor);
        citaConfirmada.setFechaHora(LocalDateTime.of(2025, 7, 11, 11, 30).atZone(ZoneId.systemDefault()).toInstant());
        citaConfirmada.setEstado(EstadoCitas.CONFIRMADA);
        citaConfirmada.setTipoCita(testTipoCita);
        citaConfirmada.setEsAutenticada(true);
        
        when(citasRepository.findById(idCita)).thenReturn(Optional.of(citaConfirmada));
        when(citasRepository.save(any(Cita.class))).thenReturn(citaConfirmada);
        
        serviciosCita.completarCita(idCita);
        
        verify(citasRepository).findById(idCita);
        verify(citasRepository).save(any(Cita.class));
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

        LocalDate fecha = LocalDate.now().plusDays(1); // Fecha futura
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

        when(userRepository.findByIdNumber(idDoctor)).thenReturn(Optional.of(testDoctor));
        when(tipoCitaRepository.findById(idTipoCita)).thenReturn(Optional.of(testTipoCita));
        when(serviciosDisponibilidadDoctor.validarDisponibilidadDoctor(anyString(), any(LocalDate.class), any(LocalTime.class))).thenReturn(true);
        when(citasRepository.save(any(Cita.class))).thenReturn(testCita);

        serviciosCita.crearCitaNoAutenticada(crearCitaNoAutenticadaDTO);

        verify(userRepository).findByIdNumber(idDoctor);
        verify(tipoCitaRepository).findById(idTipoCita);
        verify(serviciosDisponibilidadDoctor).validarDisponibilidadDoctor(anyString(), any(LocalDate.class), any(LocalTime.class));
        verify(citasRepository).save(any(Cita.class));
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

        LocalDate fecha = LocalDate.now().plusDays(2); // Fecha futura
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

        Cita citaNoAutenticada = new Cita();
        citaNoAutenticada.setId(idCita);
        citaNoAutenticada.setEsAutenticada(false);
        citaNoAutenticada.setEstado(EstadoCitas.PENDIENTE);

        when(citasRepository.findById(idCita)).thenReturn(Optional.of(citaNoAutenticada));
        when(userRepository.findByIdNumber(idDoctor)).thenReturn(Optional.of(testDoctor));
        when(tipoCitaRepository.findById(1L)).thenReturn(Optional.of(testTipoCita));
        when(citasRepository.save(any(Cita.class))).thenReturn(citaNoAutenticada);

        serviciosCita.editarCitaNoAutenticadaAdmin(idCita, editarCitaNoAutenticadaAdminDTO);

        verify(citasRepository).findById(idCita);
        verify(userRepository).findByIdNumber(idDoctor);
        verify(tipoCitaRepository).findById(1L);
        verify(citasRepository).save(any(Cita.class));
    }

    @Test
    void cancelarCitaNoAutenticadaAdmin(){
        Long idCita = 34L;
        
        Cita citaNoAutenticada = new Cita();
        citaNoAutenticada.setId(idCita);
        citaNoAutenticada.setEsAutenticada(false);
        citaNoAutenticada.setEstado(EstadoCitas.PENDIENTE);

        when(citasRepository.findById(idCita)).thenReturn(Optional.of(citaNoAutenticada));
        when(citasRepository.save(any(Cita.class))).thenReturn(citaNoAutenticada);

        serviciosCita.cancelarCitaNoAutenticadaAdmin(idCita);

        verify(citasRepository).findById(idCita);
        verify(citasRepository).save(any(Cita.class));
    }

    @Test
    void cambiarEstadoCitaNoAutenticadaAdmin (){
        Long idCita = 34L;
        
        Cita citaNoAutenticada = new Cita();
        citaNoAutenticada.setId(idCita);
        citaNoAutenticada.setEsAutenticada(false);
        citaNoAutenticada.setEstado(EstadoCitas.PENDIENTE);

        when(citasRepository.findById(idCita)).thenReturn(Optional.of(citaNoAutenticada));
        when(citasRepository.save(any(Cita.class))).thenReturn(citaNoAutenticada);

        serviciosCita.cambiarEstadoCitaNoAutenticadaAdmin(idCita, EstadoCitas.COMPLETADA);

        verify(citasRepository).findById(idCita);
        verify(citasRepository).save(any(Cita.class));
    }


    @Test
    void obtenerCitasNoAutenticadasPorPaciente(){
        String idPaciente = "1001277431";
        
        when(citasRepository.findByNumeroIdentificacionNoAutenticadoAndEsAutenticadaFalse(idPaciente)).thenReturn(List.of());

        serviciosCita.obtenerCitasNoAutenticadasPorPaciente(idPaciente);

        verify(citasRepository).findByNumeroIdentificacionNoAutenticadoAndEsAutenticadaFalse(idPaciente);
    }

    @Test
    void obtenerCitasNoAutenticadasPorDoctor (){
        String idDoctor = "111111111";
        
        when(citasRepository.findByDoctor_IdNumberAndEsAutenticadaFalse(idDoctor)).thenReturn(List.of());

        serviciosCita.obtenerCitasNoAutenticadasPorDoctor(idDoctor);

        verify(citasRepository).findByDoctor_IdNumberAndEsAutenticadaFalse(idDoctor);
    }


    


}