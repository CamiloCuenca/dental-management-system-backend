package edu.uniquindio.dentalmanagementsystembackend.Historial;

import edu.uniquindio.dentalmanagementsystembackend.dto.historial.CrearHistorialDTO;
import edu.uniquindio.dentalmanagementsystembackend.entity.Account.HistorialMedico;
import edu.uniquindio.dentalmanagementsystembackend.service.Interfaces.HistorialService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@SpringBootTest
@Transactional
public class HistorialTest {

    @Autowired
    private HistorialService historialService;
    
    @Test
    public void crearHistorialTest() {
        // Arrange
        CrearHistorialDTO dto = new CrearHistorialDTO(
            222000222L,  // ID del paciente (Diego Velásquez)
            222222222L,  // ID del odontólogo (Beatriz Martínez)
            2L,          // ID de la cita confirmada
            LocalDate.now(),
            "Paciente presenta caries en molar superior derecho",
            "Se realizará empaste dental y aplicación de flúor",
            "Paciente presenta buena higiene oral general",
            LocalDate.now().plusMonths(1)
        );

        // Act
        HistorialMedico historial = historialService.crearHistorial(dto);


    }
}
