package edu.uniquindio.dentalmanagementsystembackend.Historial;

import edu.uniquindio.dentalmanagementsystembackend.dto.historial.CrearHistorialDTO;
import edu.uniquindio.dentalmanagementsystembackend.entity.Account.HistorialMedico;
import edu.uniquindio.dentalmanagementsystembackend.service.Interfaces.HistorialService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
public class HistorialTest {

    @Autowired
    private HistorialService historialService;
    
    @Test
    public void crearHistorialTest() {
        // Arrange
        CrearHistorialDTO dto = new CrearHistorialDTO(
            555666777L,  // ID del paciente (Carlos Ramírez)
            123456789L,  // ID del odontólogo (Juan Pérez)
            1L,          // ID de la cita confirmada
            LocalDate.now(),
            "Paciente presenta caries en molar superior derecho",
            "Se realizará empaste dental y aplicación de flúor",
            "Paciente presenta buena higiene oral general",
            LocalDate.now().plusMonths(1)
        );

        // Act
        HistorialMedico historial = historialService.crearHistorial(dto);

        // Assert
        assertNotNull(historial);
        assertNotNull(historial.getId());
        assertEquals(dto.diagnostico(), historial.getDiagnostico());
        assertEquals(dto.tratamiento(), historial.getTratamiento());
        assertEquals(dto.observaciones(), historial.getObservaciones());
        assertEquals(dto.fecha(), historial.getFecha());
        assertEquals(dto.proximaCita(), historial.getProximaCita());
        assertNotNull(historial.getPaciente());
        assertNotNull(historial.getOdontologo());
        assertNotNull(historial.getCita());
    }

    @Test
    public void obtenerHistorialPorPacienteTest() {
        // Arrange
        Long pacienteId = 555666777L; // ID de Carlos Ramírez

        // Act
        List<HistorialMedico> historiales = historialService.obtenerHistorialPorPaciente(pacienteId);

        // Assert
        assertNotNull(historiales);
        assertFalse(historiales.isEmpty());
        
        // Verificar que todos los historiales pertenecen al paciente correcto
        historiales.forEach(historial -> {
            assertEquals(pacienteId.toString(), historial.getPaciente().getIdNumber());
        });

        // Verificar que los historiales están ordenados por fecha descendente
        for (int i = 1; i < historiales.size(); i++) {
            assertTrue(historiales.get(i-1).getFecha().isAfter(historiales.get(i).getFecha()) || 
                      historiales.get(i-1).getFecha().isEqual(historiales.get(i).getFecha()));
        }
    }
}
