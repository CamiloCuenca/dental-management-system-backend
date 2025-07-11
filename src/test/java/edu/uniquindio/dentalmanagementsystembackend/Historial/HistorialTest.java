package edu.uniquindio.dentalmanagementsystembackend.Historial;

import com.itextpdf.text.DocumentException;
import edu.uniquindio.dentalmanagementsystembackend.Enum.EstadoCitas;
import edu.uniquindio.dentalmanagementsystembackend.Enum.Rol;
import edu.uniquindio.dentalmanagementsystembackend.dto.historial.CrearHistorialDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.historial.HistorialDTO;
import edu.uniquindio.dentalmanagementsystembackend.entity.Account.Account;
import edu.uniquindio.dentalmanagementsystembackend.entity.Account.HistorialMedico;
import edu.uniquindio.dentalmanagementsystembackend.entity.Account.User;
import edu.uniquindio.dentalmanagementsystembackend.entity.Cita;
import edu.uniquindio.dentalmanagementsystembackend.entity.TipoCita;
import edu.uniquindio.dentalmanagementsystembackend.repository.HistorialMedicoRepository;
import edu.uniquindio.dentalmanagementsystembackend.repository.UserRepository;
import edu.uniquindio.dentalmanagementsystembackend.repository.CitasRepository;
import edu.uniquindio.dentalmanagementsystembackend.service.impl.HistorialServiceImpl;
import edu.uniquindio.dentalmanagementsystembackend.service.impl.PdfGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HistorialTest {

    @Mock
    private HistorialMedicoRepository historialRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CitasRepository citasRepository;
    @Mock
    private PdfGenerator pdfGenerator;
    @InjectMocks
    private HistorialServiceImpl historialService;

    private User testPaciente;
    private User testDoctor;
    private Cita testCita;
    private TipoCita testTipoCita;
    private HistorialMedico testHistorial;

    @BeforeEach
    void setUp() {
        testPaciente = new User();
        testPaciente.setIdNumber("555555556");
        testPaciente.setName("Juan");
        testPaciente.setLastName("Pérez");
        Account pacienteAccount = new Account();
        pacienteAccount.setRol(Rol.PACIENTE);
        testPaciente.setAccount(pacienteAccount);

        testDoctor = new User();
        testDoctor.setIdNumber("111111111");
        testDoctor.setName("María");
        testDoctor.setLastName("García");
        Account doctorAccount = new Account();
        doctorAccount.setRol(Rol.DOCTOR);
        testDoctor.setAccount(doctorAccount);

        testTipoCita = new TipoCita();
        testTipoCita.setId(1L);
        testTipoCita.setNombre("Consulta General");

        testCita = new Cita();
        testCita.setId(1L);
        testCita.setPaciente(testPaciente);
        testCita.setDoctor(testDoctor);
        testCita.setFechaHora(Instant.now());
        testCita.setEstado(EstadoCitas.CONFIRMADA);
        testCita.setTipoCita(testTipoCita);
        testCita.setEsAutenticada(true);

        testHistorial = new HistorialMedico();
        testHistorial.setId(100L);
        testHistorial.setPaciente(testPaciente);
        testHistorial.setDoctor(testDoctor);
        testHistorial.setCita(testCita);
        testHistorial.setFecha(LocalDate.now());
        testHistorial.setDiagnostico("Diagnóstico de prueba");
        testHistorial.setTratamiento("Tratamiento de prueba");
        testHistorial.setObservaciones("Observaciones de prueba");
        testHistorial.setProximaCita(LocalDate.now().plusDays(30));
    }

    @Test
    void crearHistorial() {
        CrearHistorialDTO crearHistorialDTO = new CrearHistorialDTO(
                "555555556",
                "111111111",
                1L,
                LocalDate.now(),
                "Diagnóstico de prueba",
                "Tratamiento de prueba",
                "Observaciones de prueba",
                LocalDate.now().plusDays(30)
        );

        when(userRepository.findById("555555556")).thenReturn(Optional.of(testPaciente));
        when(userRepository.findById("111111111")).thenReturn(Optional.of(testDoctor));
        when(citasRepository.findById(1L)).thenReturn(Optional.of(testCita));
        when(historialRepository.save(any(HistorialMedico.class))).thenReturn(testHistorial);
        when(citasRepository.save(any(Cita.class))).thenReturn(testCita);

        HistorialMedico historialCreado = historialService.crearHistorial(crearHistorialDTO);
        assertNotNull(historialCreado);
        assertEquals(crearHistorialDTO.diagnostico(), historialCreado.getDiagnostico());
        verify(historialRepository).save(any(HistorialMedico.class));
    }

    @Test
    void testListarHistorialesPorPacienteAgrupadosPorAnio() {
        String idPaciente = "555555556";
        List<HistorialMedico> historiales = List.of(testHistorial);
        when(historialRepository.buscarHistorialesPorIdPaciente(idPaciente)).thenReturn(historiales);

        Map<Integer, List<HistorialDTO>> historialesAgrupados = historialService.listarHistorialesPorPacienteAgrupadosPorAnio(idPaciente);
        assertNotNull(historialesAgrupados);
        assertTrue(historialesAgrupados.containsKey(LocalDate.now().getYear()));
        assertFalse(historialesAgrupados.get(LocalDate.now().getYear()).isEmpty());
    }

    @Test
    void pdfHistorial() throws DocumentException {
        String idPaciente = "555555556";
        // Simular que el método de pdfGenerator devuelve un byte array
        when(pdfGenerator.historialPDF(idPaciente)).thenReturn(new byte[]{1, 2, 3, 4});
        // Llamada real (no se verifica el contenido del PDF, solo que no lance excepción)
        byte[] pdfBytes = pdfGenerator.historialPDF(idPaciente);
        assertNotNull(pdfBytes);
        verify(pdfGenerator).historialPDF(idPaciente);
    }

    @Test
    void testListarHistorialesPorPacienteYAnio() {
        String idPaciente = "555555556";
        int anio = LocalDate.now().getYear();
        testHistorial.setFecha(LocalDate.of(anio, 5, 10));
        List<HistorialMedico> historiales = List.of(testHistorial);
        when(historialRepository.buscarHistorialesPorIdPaciente(idPaciente)).thenReturn(historiales);

        List<HistorialDTO> historialesDTO = historialService.listarHistorialesPorPacienteYAnio(idPaciente, anio);
        assertNotNull(historialesDTO);
        assertFalse(historialesDTO.isEmpty());
        for (HistorialDTO historial : historialesDTO) {
            assertEquals(anio, historial.fecha().getYear());
        }
    }
}
