package edu.uniquindio.dentalmanagementsystembackend.Historial;

import com.itextpdf.text.DocumentException;
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


    /**
     * Prueba para crear un nuevo historial médico.
     * Se verifica que el historial se haya creado correctamente y que los datos sean los esperados.
     */
    @Test
    public void crearHistorial(){
        // Crear un DTO de historial médico
        CrearHistorialDTO crearHistorialDTO = new CrearHistorialDTO("555555556",
                "111111111",
                1L,
                LocalDate.now(),
                "Diagnóstico de prueba",
                "Tratamiento de prueba",
                "Observaciones de prueba",
                LocalDate.now().plusDays(30));

        // Llamar al servicio para crear el historial
        HistorialMedico historialCreado = historialService.crearHistorial(crearHistorialDTO);

        // Verificar que el historial se haya creado correctamente
        assertNotNull(historialCreado);
        assertEquals(crearHistorialDTO.diagnostico(), historialCreado.getDiagnostico());
    }


    @Test
    public void testListarHistorialesPorPacienteAgrupadosPorAnio() {
        // ID del paciente para la prueba
        String idPaciente = "555555556";

        // Llamar al servicio para listar los historiales agrupados por año
        Map<Integer, List<HistorialDTO>> historialesAgrupados = historialService.listarHistorialesPorPacienteAgrupadosPorAnio(idPaciente);

        System.out.println(historialesAgrupados);

    }

    @Test
    public void pdfHistorial () throws DocumentException {
        // ID del paciente para la prueba
        String idPaciente = "555555556";

        pdfGenerator.historialPDF(idPaciente);

    }

    @Test
    public void testListarHistorialesPorPacienteYAnio() {
        // ID del paciente y año para la prueba
        String idPaciente = "555555556";
        int anio = 2024;

        // Llamar al servicio para listar los historiales por paciente y año
        List<HistorialDTO> historiales = historialService.listarHistorialesPorPacienteYAnio(idPaciente, anio);

        // Verificar que se obtuvieron los historiales
        assertNotNull(historiales);
        assertFalse(historiales.isEmpty());

        // Verificar que todos los historiales sean del año especificado
        for (HistorialDTO historial : historiales) {
            assertEquals(anio, historial.fecha().getYear());
        }

        System.out.println(historiales);
    }
}
