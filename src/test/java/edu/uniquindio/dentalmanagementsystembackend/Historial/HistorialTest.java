package edu.uniquindio.dentalmanagementsystembackend.Historial;

import edu.uniquindio.dentalmanagementsystembackend.dto.historial.CrearHistorialDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.historial.HistorialDTO;
import edu.uniquindio.dentalmanagementsystembackend.entity.Account.HistorialMedico;
import edu.uniquindio.dentalmanagementsystembackend.entity.Account.User;
import edu.uniquindio.dentalmanagementsystembackend.entity.Cita;
import edu.uniquindio.dentalmanagementsystembackend.repository.HistorialMedicoRepository;
import edu.uniquindio.dentalmanagementsystembackend.repository.UserRepository;
import edu.uniquindio.dentalmanagementsystembackend.repository.CitasRepository;
import edu.uniquindio.dentalmanagementsystembackend.service.Interfaces.HistorialService;
import edu.uniquindio.dentalmanagementsystembackend.exception.HistorialException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase de pruebas para el servicio de historial médico.
 * Contiene pruebas unitarias para verificar el funcionamiento correcto de todos los métodos
 * del servicio de historial médico.
 */
@SpringBootTest
@Transactional
public class HistorialTest {

    @Autowired
    private HistorialService historialService;

    @Autowired
    private HistorialMedicoRepository historialRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CitasRepository citasRepository;

    /**
     * Prueba la creación de un nuevo historial médico.
     * Verifica que el historial se cree correctamente con todos sus campos.
     */
    @Test
    public void crearHistorialTest() {
        // Arrange
        CrearHistorialDTO dto = new CrearHistorialDTO(
            1L, // ID del paciente
            2L, // ID del odontólogo
            1L, // ID de la cita
            LocalDate.now(),
            "Diagnóstico de prueba",
            "Tratamiento de prueba",
            "Observaciones de prueba",
            LocalDate.now().plusDays(7)
        );

        // Act
        HistorialMedico historial = historialService.crearHistorial(dto);

        // Assert
        assertNotNull(historial);
        assertEquals(dto.diagnostico(), historial.getDiagnostico());
        assertEquals(dto.tratamiento(), historial.getTratamiento());
        assertEquals(dto.observaciones(), historial.getObservaciones());
        assertEquals(dto.fecha(), historial.getFecha());
        assertEquals(dto.proximaCita(), historial.getProximaCita());
        assertNotNull(historial.getPaciente());
        assertNotNull(historial.getOdontologo());
        assertNotNull(historial.getCita());
    }

    /**
     * Prueba la obtención de historiales médicos por paciente.
     * Verifica que se retornen correctamente los historiales asociados a un paciente específico.
     */
    @Test
    public void obtenerHistorialPorPacienteTest() {
        // Arrange
        Long pacienteId = 1L;

        // Act
        List<HistorialMedico> historiales = historialService.obtenerHistorialPorPaciente(pacienteId);

        // Assert
        assertNotNull(historiales);
        assertFalse(historiales.isEmpty());
        historiales.forEach(historial -> 
            assertEquals(pacienteId.toString(), historial.getPaciente().getIdNumber())
        );
    }

    /**
     * Prueba la obtención de historiales médicos en formato DTO por paciente.
     * Verifica que los DTOs contengan toda la información necesaria.
     */
    @Test
    public void obtenerHistorialesDTOPorPacienteTest() {
        // Arrange
        Long pacienteId = 1L;

        // Act
        List<HistorialDTO> historiales = historialService.obtenerHistorialesDTOPorPaciente(pacienteId);

        // Assert
        assertNotNull(historiales);
        assertFalse(historiales.isEmpty());
        historiales.forEach(dto -> {
            assertNotNull(dto.id());
            assertNotNull(dto.nombrePaciente());
            assertNotNull(dto.nombreOdontologo());
            assertNotNull(dto.fecha());
            assertNotNull(dto.diagnostico());
            assertNotNull(dto.tratamiento());
        });
    }

    /**
     * Prueba la obtención de un historial médico específico por su ID.
     * Verifica que se retorne el historial correcto con todos sus campos.
     */
    @Test
    public void obtenerHistorialPorIdTest() {
        // Arrange
        Long historialId = 1L;

        // Act
        HistorialDTO historial = historialService.obtenerHistorialPorId(historialId);

        // Assert
        assertNotNull(historial);
        assertEquals(historialId, historial.id());
        assertNotNull(historial.nombrePaciente());
        assertNotNull(historial.nombreOdontologo());
        assertNotNull(historial.fecha());
        assertNotNull(historial.diagnostico());
        assertNotNull(historial.tratamiento());
    }

    /**
     * Prueba la obtención de historiales médicos por fecha.
     * Verifica que se retornen correctamente los historiales de una fecha específica.
     */
    @Test
    public void obtenerHistorialesPorFechaTest() {
        // Arrange
        LocalDate fecha = LocalDate.now();

        // Act
        List<HistorialDTO> historiales = historialService.obtenerHistorialesPorFecha(fecha);

        // Assert
        assertNotNull(historiales);
        historiales.forEach(dto -> {
            assertEquals(fecha, dto.fecha());
            assertNotNull(dto.id());
            assertNotNull(dto.nombrePaciente());
            assertNotNull(dto.nombreOdontologo());
            assertNotNull(dto.diagnostico());
            assertNotNull(dto.tratamiento());
        });
    }

    /**
     * Prueba la obtención de historiales médicos por odontólogo.
     * Verifica que se retornen correctamente los historiales creados por un odontólogo específico.
     */
    @Test
    public void obtenerHistorialesPorOdontologoTest() {
        // Arrange
        Long odontologoId = 2L;

        // Act
        List<HistorialDTO> historiales = historialService.obtenerHistorialesPorOdontologo(odontologoId);

        // Assert
        assertNotNull(historiales);
        historiales.forEach(dto -> {
            assertNotNull(dto.id());
            assertNotNull(dto.nombrePaciente());
            assertNotNull(dto.nombreOdontologo());
            assertNotNull(dto.fecha());
            assertNotNull(dto.diagnostico());
            assertNotNull(dto.tratamiento());
        });
    }

    /**
     * Prueba la obtención de todos los historiales médicos.
     * Verifica que se retornen todos los historiales con sus campos completos.
     */
    @Test
    public void obtenerTodosLosHistorialesTest() {
        // Act
        List<HistorialMedico> historiales = historialService.obtenerTodosLosHistoriales();
        List<HistorialDTO> historialesDTO = historiales.stream()
                .map(historial -> new HistorialDTO(
                    historial.getId(),
                    historial.getPaciente().getName() + " " + historial.getPaciente().getLastName(),
                    historial.getOdontologo().getName() + " " + historial.getOdontologo().getLastName(),
                    historial.getFecha(),
                    historial.getDiagnostico(),
                    historial.getTratamiento(),
                    historial.getObservaciones(),
                    historial.getProximaCita(),
                    historial.getCita().getTipoCita().toString()
                ))
                .collect(Collectors.toList());

        // Assert
        assertNotNull(historialesDTO);
        historialesDTO.forEach(dto -> {
            assertNotNull(dto.id());
            assertNotNull(dto.nombrePaciente());
            assertNotNull(dto.nombreOdontologo());
            assertNotNull(dto.fecha());
            assertNotNull(dto.diagnostico());
            assertNotNull(dto.tratamiento());
        });
    }
}
