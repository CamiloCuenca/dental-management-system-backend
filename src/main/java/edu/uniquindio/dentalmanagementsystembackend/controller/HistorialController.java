package edu.uniquindio.dentalmanagementsystembackend.controller;

import edu.uniquindio.dentalmanagementsystembackend.dto.historial.CrearHistorialDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.historial.HistorialDTO;
import edu.uniquindio.dentalmanagementsystembackend.entity.Account.HistorialMedico;
import edu.uniquindio.dentalmanagementsystembackend.service.Interfaces.HistorialService;
import edu.uniquindio.dentalmanagementsystembackend.service.impl.PdfGenerator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/historiales")
@RequiredArgsConstructor
public class HistorialController {


    // 1. Declara el logger
    private static final Logger logger = LoggerFactory.getLogger(HistorialController.class);


    private final HistorialService historialService;


    private final  PdfGenerator pdfGenerator;

    /**
     * Endpoint para crear un nuevo historial médico.
     *
     * @param crearHistorialDTO DTO con los datos del historial
     * @return ResponseEntity con el historial creado
     */
    @PostMapping("/crear")
    public ResponseEntity<HistorialMedico> crearHistorial(@Valid @RequestBody CrearHistorialDTO crearHistorialDTO) {
        HistorialMedico historialCreado = historialService.crearHistorial(crearHistorialDTO);
        return ResponseEntity.ok(historialCreado);
    }

    @GetMapping("/paciente/{idPaciente}/agrupado-por-anio")
    public ResponseEntity<Map<Integer, List<HistorialDTO>>> listarHistorialesPorPacienteAgrupadosPorAnio(
            @PathVariable("idPaciente") String idNumber) {
        Map<Integer, List<HistorialDTO>> historialAgrupado = historialService.listarHistorialesPorPacienteAgrupadosPorAnio(idNumber);
        return ResponseEntity.ok(historialAgrupado);
    }

    @GetMapping("/paciente/{idPaciente}/anio/{anio}")
    public ResponseEntity<List<HistorialDTO>> listarHistorialesPorPacienteYAnio(
            @PathVariable("idPaciente") String idNumber,
            @PathVariable("anio") int anio) {
        List<HistorialDTO> historiales = historialService.listarHistorialesPorPacienteYAnio(idNumber, anio);
        return ResponseEntity.ok(historiales);
    }

    @PostMapping("/paciente/pdf/{id}")
    public ResponseEntity<byte[]> generarPdfHistorial(@PathVariable("id") String id) {
        try {
            // Validación básica del ID
            if (id == null || id.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("ID de paciente no válido".getBytes());
            }

            byte[] pdfBytes = pdfGenerator.historialPDF(id);

            // Verificar que el PDF no esté vacío
            if (pdfBytes == null || pdfBytes.length == 0) {
                throw new IllegalStateException("El PDF generado está vacío");
            }

            return ResponseEntity.ok()
                    .header("Content-Type", "application/pdf")
                    .header("Content-Disposition", "attachment; filename=historial_"+id+".pdf")
                    .body(pdfBytes);

        } catch (Exception e) {
            // Log del error completo
            logger.error("Error al generar PDF para paciente " + id, e);

            // Devuelve un mensaje de error claro
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error al generar PDF: " + e.getMessage()).getBytes());
        }
    }


}
