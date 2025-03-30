package edu.uniquindio.dentalmanagementsystembackend.controller;

import edu.uniquindio.dentalmanagementsystembackend.Enum.TipoCita;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.ListaCitasDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.CitaDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.DoctorDisponibilidadDTO;
import edu.uniquindio.dentalmanagementsystembackend.service.Interfaces.ServiciosCitas;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/api/citas")
public class CitaController {


    private final ServiciosCitas servicioCita;

    // Constructor para inyectar el servicio de citas
    public CitaController(ServiciosCitas servicioCita) {
        this.servicioCita = servicioCita;
    }

    // Endpoint para crear una nueva cita
    @PostMapping("/crear")
    public ResponseEntity<String> crearCita(@RequestBody CitaDTO citaDTO) throws Exception {
        servicioCita.crearCita(citaDTO);
        return ResponseEntity.ok("Cita creada exitosamente.");
    }

    // Endpoint para obtener las citas de un paciente específico
    @GetMapping("/paciente/{idPaciente}")
    public ResponseEntity<List<ListaCitasDTO>> obtenerCitasPorPaciente(@PathVariable Long idPaciente) {
        List<ListaCitasDTO> citas = servicioCita.obtenerCitasPorPaciente(idPaciente);
        return ResponseEntity.ok(citas);
    }

    // Endpoint para obtener todas las citas de los doctores
    @GetMapping("/doctor")
    public ResponseEntity<List<ListaCitasDTO>> ObtenerCitas() {
        List<ListaCitasDTO> citas = servicioCita.obtenerTodasLasCitas();
        return ResponseEntity.ok(citas);
    }

    // Endpoint para editar una cita existente
    @PutMapping("/editar/{idCita}")
    public ResponseEntity<String> editarCita(@PathVariable Long idCita, @RequestParam TipoCita nuevoTipoCita) {
        servicioCita.editarCita(idCita, nuevoTipoCita);
        return ResponseEntity.ok("Cita actualizada correctamente.");
    }

    // Endpoint para cancelar una cita existente
    @PutMapping("/cancelar/{idCita}")
    public ResponseEntity<String> cancelarCita(@PathVariable Long idCita) {
        servicioCita.cancelarCita(idCita);
        return ResponseEntity.ok("Cita cancelada correctamente.");
    }

    // Endpoint para obtener las fechas disponibles de todos los doctores
    @GetMapping("/fechas-disponibles")
    public ResponseEntity<List<DoctorDisponibilidadDTO>> obtenerFechasDisponiblesDoctores() {
        List<DoctorDisponibilidadDTO> disponibilidadDoctores = servicioCita.obtenerFechasDisponiblesDoctores();
        return ResponseEntity.ok(disponibilidadDoctores);
    }


}
