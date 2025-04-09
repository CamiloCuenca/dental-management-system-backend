package edu.uniquindio.dentalmanagementsystembackend.controller;

import edu.uniquindio.dentalmanagementsystembackend.dto.historial.CrearHistorialDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.historial.HistorialDTO;
import edu.uniquindio.dentalmanagementsystembackend.entity.Account.HistorialMedico;
import edu.uniquindio.dentalmanagementsystembackend.service.Interfaces.HistorialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/historiales")
@RequiredArgsConstructor
public class HistorialController {

    private final HistorialService historialService;

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


}
