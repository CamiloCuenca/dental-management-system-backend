package edu.uniquindio.dentalmanagementsystembackend.controller;

import edu.uniquindio.dentalmanagementsystembackend.dto.cita.CitaDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.ListaCitasDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.DoctorDisponibilidadDTO;
import edu.uniquindio.dentalmanagementsystembackend.Enum.EstadoCitas;
import edu.uniquindio.dentalmanagementsystembackend.Enum.TipoCita;
import edu.uniquindio.dentalmanagementsystembackend.service.Interfaces.ServiciosCitas;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/citas")
@RequiredArgsConstructor
public class CitasController {

    private final ServiciosCitas serviciosCitas;

    @PostMapping("/crear")
    public ResponseEntity<Void> crearCita(@RequestBody CitaDTO citaDTO) throws Exception {
        serviciosCitas.crearCita(citaDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/paciente/{idPaciente}")
    public ResponseEntity<List<ListaCitasDTO>> obtenerCitasPorPaciente(@PathVariable Long idPaciente) {
        return ResponseEntity.ok(serviciosCitas.obtenerCitasPorPaciente(idPaciente));
    }

    @GetMapping("/todas")
    public ResponseEntity<List<ListaCitasDTO>> obtenerTodasLasCitas() {
        return ResponseEntity.ok(serviciosCitas.obtenerTodasLasCitas());
    }

    @PutMapping("/{idCita}/tipo")
    public ResponseEntity<Void> editarCita(
            @PathVariable Long idCita,
            @RequestParam TipoCita nuevoTipoCita) {
        serviciosCitas.editarCita(idCita, nuevoTipoCita);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{idCita}/cancelar")
    public ResponseEntity<Void> cancelarCita(@PathVariable Long idCita) {
        serviciosCitas.cancelarCita(idCita);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/disponibilidad/doctores")
    public ResponseEntity<List<DoctorDisponibilidadDTO>> obtenerFechasDisponiblesDoctores() {
        return ResponseEntity.ok(serviciosCitas.obtenerFechasDisponiblesDoctores());
    }

    @PutMapping("/{idCita}/confirmar")
    public ResponseEntity<Void> confirmarCita(@PathVariable Long idCita) {
        serviciosCitas.confirmarCita(idCita);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{idCita}/completar")
    public ResponseEntity<Void> completarCita(@PathVariable Long idCita) {
        serviciosCitas.completarCita(idCita);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/fecha/{fecha}")
    public ResponseEntity<List<ListaCitasDTO>> obtenerCitasPorFecha(@PathVariable LocalDate fecha) {
        return ResponseEntity.ok(serviciosCitas.obtenerCitasPorFecha(fecha));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<ListaCitasDTO>> obtenerCitasPorEstado(@PathVariable EstadoCitas estado) {
        return ResponseEntity.ok(serviciosCitas.obtenerCitasPorEstado(estado));
    }

    @PutMapping("/{idCita}/reprogramar")
    public ResponseEntity<Void> reprogramarCita(
            @PathVariable Long idCita,
            @RequestParam LocalDateTime nuevaFechaHora) {
        serviciosCitas.reprogramarCita(idCita, nuevaFechaHora);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/estadisticas/estado")
    public ResponseEntity<Map<EstadoCitas, Long>> obtenerEstadisticasCitasPorEstado() {
        return ResponseEntity.ok(serviciosCitas.obtenerEstadisticasCitasPorEstado());
    }

    @GetMapping("/estadisticas/doctor")
    public ResponseEntity<Map<Long, Long>> obtenerEstadisticasCitasPorDoctor() {
        return ResponseEntity.ok(serviciosCitas.obtenerEstadisticasCitasPorDoctor());
    }

    @PostMapping("/{idCita}/recordatorio")
    public ResponseEntity<Void> enviarRecordatorioCita(@PathVariable Long idCita) {
        serviciosCitas.enviarRecordatorioCita(idCita);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/disponibilidad/doctor/{doctorId}")
    public ResponseEntity<DoctorDisponibilidadDTO> obtenerFechasDisponiblesDoctor(
            @PathVariable Long doctorId) {
        return ResponseEntity.ok(serviciosCitas.obtenerFechasDisponiblesDoctor(doctorId));
    }

    @GetMapping("/disponibilidad/tipo-doctor/{tipoDoctor}")
    public ResponseEntity<List<DoctorDisponibilidadDTO>> obtenerFechasDisponiblesPorTipoDoctor(
            @PathVariable Long tipoDoctor) {
        return ResponseEntity.ok(serviciosCitas.obtenerFechasDisponiblesPorTipoDoctor(tipoDoctor));
    }
} 