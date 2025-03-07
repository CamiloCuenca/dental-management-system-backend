package edu.uniquindio.dentalmanagementsystembackend.controller;

import edu.uniquindio.dentalmanagementsystembackend.dto.CitaDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.ListaCitasDTO;
import edu.uniquindio.dentalmanagementsystembackend.service.Interfaces.ServiciosCitas;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/api/citas")
public class CitaController {

    private final ServiciosCitas servicioCita;

    public CitaController(ServiciosCitas servicioCita) {
        this.servicioCita = servicioCita;
    }

    @PostMapping("/crear")
    public ResponseEntity<String> crearCita(@RequestBody CitaDTO citaDTO) {
        servicioCita.crearCita(citaDTO);
        return ResponseEntity.ok("Cita creada exitosamente.");
    }
    @GetMapping("/paciente/{idPaciente}")
    public ResponseEntity<List<ListaCitasDTO>> obtenerCitasPorPaciente(@PathVariable Long idPaciente) {
        List<ListaCitasDTO> citas = servicioCita.obtenerCitasPorPaciente(idPaciente);
        return ResponseEntity.ok(citas);
    }
}
