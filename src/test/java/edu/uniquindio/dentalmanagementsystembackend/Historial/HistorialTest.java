package edu.uniquindio.dentalmanagementsystembackend.Historial;

import edu.uniquindio.dentalmanagementsystembackend.dto.historial.CrearHistorialDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.historial.HistorialDTO;
import edu.uniquindio.dentalmanagementsystembackend.entity.Account.HistorialMedico;
import edu.uniquindio.dentalmanagementsystembackend.repository.HistorialMedicoRepository;
import edu.uniquindio.dentalmanagementsystembackend.repository.UserRepository;
import edu.uniquindio.dentalmanagementsystembackend.repository.CitasRepository;
import edu.uniquindio.dentalmanagementsystembackend.service.Interfaces.HistorialService;
import edu.uniquindio.dentalmanagementsystembackend.service.impl.PdfGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
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

    @Autowired
    private PdfGenerator pdfGenerator;



    @Test
    public void testListarHistorialesPorPacienteAgrupadosPorAnio() {
        // ID del paciente para la prueba
        String idPaciente = "555555556";

        // Llamar al servicio para listar los historiales agrupados por año
        Map<Integer, List<HistorialDTO>> historialesAgrupados = historialService.listarHistorialesPorPacienteAgrupadosPorAnio(idPaciente);

        System.out.println(historialesAgrupados);

    }

    @Test
    public void pdfHistorial (){
        // ID del paciente para la prueba
        String idPaciente = "555555556";

        pdfGenerator.historialPDF(idPaciente);

    }
}
