package edu.uniquindio.dentalmanagementsystembackend.controller;

import edu.uniquindio.dentalmanagementsystembackend.Enum.TipoCita;
import edu.uniquindio.dentalmanagementsystembackend.Enum.EstadoCitas;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.ListaCitasDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.CitaDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.DoctorDisponibilidadDTO;
import edu.uniquindio.dentalmanagementsystembackend.service.Interfaces.ServiciosCitas;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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

    // Endpoint para confirmar una cita
    @PutMapping("/confirmar/{idCita}")
    public ResponseEntity<String> confirmarCita(@PathVariable Long idCita) {
        servicioCita.confirmarCita(idCita);
        return ResponseEntity.ok("Cita confirmada exitosamente.");
    }

    // Endpoint para marcar una cita como completada
    @PutMapping("/completar/{idCita}")
    public ResponseEntity<String> completarCita(@PathVariable Long idCita) {
        servicioCita.completarCita(idCita);
        return ResponseEntity.ok("Cita marcada como completada.");
    }

    // Endpoint para obtener citas por fecha
    @GetMapping("/fecha/{fecha}")
    public ResponseEntity<List<ListaCitasDTO>> obtenerCitasPorFecha(@PathVariable LocalDate fecha) {
        List<ListaCitasDTO> citas = servicioCita.obtenerCitasPorFecha(fecha);
        return ResponseEntity.ok(citas);
    }

    // Endpoint para obtener citas por estado
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<ListaCitasDTO>> obtenerCitasPorEstado(@PathVariable EstadoCitas estado) {
        List<ListaCitasDTO> citas = servicioCita.obtenerCitasPorEstado(estado);
        return ResponseEntity.ok(citas);
    }


    // Endpoint para reprogramar una cita
    @PutMapping("/reprogramar/{idCita}")
    public ResponseEntity<String> reprogramarCita(
            @PathVariable Long idCita,
            @RequestParam LocalDateTime nuevaFechaHora) {
        servicioCita.reprogramarCita(idCita, nuevaFechaHora);
        return ResponseEntity.ok("Cita reprogramada exitosamente.");
    }

    // Endpoint para obtener estadísticas de citas por estado
    @GetMapping("/estadisticas/estado")
    public ResponseEntity<Map<EstadoCitas, Long>> obtenerEstadisticasCitasPorEstado() {
        Map<EstadoCitas, Long> estadisticas = servicioCita.obtenerEstadisticasCitasPorEstado();
        return ResponseEntity.ok(estadisticas);
    }

    // Endpoint para obtener estadísticas de citas por doctor
    @GetMapping("/estadisticas/doctor")
    public ResponseEntity<Map<Long, Long>> obtenerEstadisticasCitasPorDoctor() {
        Map<Long, Long> estadisticas = servicioCita.obtenerEstadisticasCitasPorDoctor();
        return ResponseEntity.ok(estadisticas);
    }

    // Endpoint para enviar recordatorio de cita
    @PostMapping("/recordatorio/{idCita}")
    public ResponseEntity<String> enviarRecordatorioCita(@PathVariable Long idCita) {
        servicioCita.enviarRecordatorioCita(idCita);
        return ResponseEntity.ok("Recordatorio de cita enviado exitosamente.");
    }


    // Endpoint para obtener las fechas disponibles de todos los doctores
    @GetMapping("/fechas-disponibles")
    public ResponseEntity<List<DoctorDisponibilidadDTO>> obtenerFechasDisponiblesDoctores() {
        try {
            List<DoctorDisponibilidadDTO> disponibilidadDoctores = servicioCita.obtenerFechasDisponiblesDoctores();
            return ResponseEntity.ok(disponibilidadDoctores);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Nuevo endpoint para obtener fechas disponibles de un doctor específico
    @GetMapping("/fechas-disponibles/doctor/{doctorId}")
    public ResponseEntity<DoctorDisponibilidadDTO> obtenerFechasDisponiblesDoctor(
            @PathVariable String doctorId) {
        try {
            DoctorDisponibilidadDTO disponibilidad = servicioCita.obtenerFechasDisponiblesDoctor(doctorId);
            return ResponseEntity.ok(disponibilidad);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Nuevo endpoint para obtener doctores y fechas disponibles por tipo de doctor
    @GetMapping("/fechas-disponibles/tipo/{tipoDoctor}")
    public ResponseEntity<List<DoctorDisponibilidadDTO>> obtenerFechasDisponiblesPorTipo(
            @PathVariable String tipoDoctor) {
        try {
            List<DoctorDisponibilidadDTO> disponibilidad =
                    servicioCita.obtenerFechasDisponiblesPorTipoDoctor(tipoDoctor);
            return ResponseEntity.ok(disponibilidad);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }




}
