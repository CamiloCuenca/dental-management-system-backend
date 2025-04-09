package edu.uniquindio.dentalmanagementsystembackend.controller;

import edu.uniquindio.dentalmanagementsystembackend.dto.cita.CitaDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.CrearCitaDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.DoctorEspecialidadDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.EditarCitaAdminDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.EditarCitaPacienteDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.FechaDisponibleDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.HorarioDisponibleDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.TipoCitaDTO;
import edu.uniquindio.dentalmanagementsystembackend.entity.Account.User;
import edu.uniquindio.dentalmanagementsystembackend.entity.Cita;
import edu.uniquindio.dentalmanagementsystembackend.entity.TipoCita;
import edu.uniquindio.dentalmanagementsystembackend.service.Interfaces.ServiciosCitas;
import edu.uniquindio.dentalmanagementsystembackend.service.Interfaces.ServiciosTipoCita;
import edu.uniquindio.dentalmanagementsystembackend.service.Interfaces.ServiciosDisponibilidadDoctor;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/citas")
@RequiredArgsConstructor
public class CitasController {

    private final ServiciosCitas serviciosCitas;
    private final ServiciosTipoCita serviciosTipoCita;
    private final ServiciosDisponibilidadDoctor serviciosDisponibilidadDoctor;
    
    /**
     * Crea una nueva cita
     * @param dto DTO con la información de la cita
     * @return Cita creada
     */
    @PostMapping("/crear")
    public ResponseEntity<Cita> crearCita(@RequestBody CrearCitaDTO dto) {
        return ResponseEntity.ok(serviciosCitas.crearCita(dto));
    }

    /**
     * Obtiene todos los tipos de cita disponibles
     * @return Lista de tipos de cita
     */
    @GetMapping("/tipos")
    public ResponseEntity<List<TipoCitaDTO>> obtenerTiposCita() {
        return ResponseEntity.ok(serviciosTipoCita.listarTiposCita());
    }

    /**
     * Obtiene los doctores disponibles para una especialidad específica
     * @param especialidadId ID de la especialidad
     * @return Lista de doctores con la especialidad especificada
     */
    @GetMapping("/doctores/{especialidadId}")
    public ResponseEntity<List<DoctorEspecialidadDTO>> obtenerDoctoresPorEspecialidad(@PathVariable Long especialidadId) {
        return ResponseEntity.ok(serviciosCitas.obtenerDoctoresPorEspecialidad(especialidadId));
    }

    /**
     * Obtiene las fechas disponibles para un doctor en un rango de fechas
     * @param doctorId ID del doctor
     * @param fechaInicio Fecha de inicio del rango
     * @param fechaFin Fecha de fin del rango
     * @return Lista de fechas disponibles con sus horarios
     */
    @GetMapping("/disponibilidad/fechas/{doctorId}")
    public ResponseEntity<List<FechaDisponibleDTO>> obtenerFechasDisponibles(
            @PathVariable String doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        return ResponseEntity.ok(serviciosDisponibilidadDoctor.obtenerFechasDisponibles(doctorId, fechaInicio, fechaFin));
    }

    /**
     * Obtiene los horarios disponibles para un doctor en una fecha específica
     * @param doctorId ID del doctor
     * @param fecha Fecha para la que se quieren obtener los horarios
     * @return Lista de horarios disponibles
     */
    @GetMapping("/disponibilidad/horarios/{doctorId}")
    public ResponseEntity<List<HorarioDisponibleDTO>> obtenerHorariosDisponibles(
            @PathVariable String doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(serviciosDisponibilidadDoctor.obtenerHorariosDisponibles(doctorId, fecha));
    }

    /**
     * Obtiene todas las citas de un paciente
     * @param idPaciente ID del paciente
     * @return Lista de citas del paciente
     */
    @GetMapping("/paciente/{idPaciente}")
    public ResponseEntity<List<CitaDTO>> obtenerCitasPorPaciente(@PathVariable String idPaciente) {
        return ResponseEntity.ok(serviciosCitas.obtenerCitasPorPaciente(idPaciente));
    }

    /**
     * Obtiene todas las citas de un doctor
     * @param idDoctor ID del doctor
     * @return Lista de citas del doctor
     */
    @GetMapping("/doctor/{idDoctor}")
    public ResponseEntity<List<CitaDTO>> obtenerCitasPorDoctor(@PathVariable String idDoctor) {
        return ResponseEntity.ok(serviciosCitas.obtenerCitasPorDoctor(idDoctor));
    }

    /**
     * Edita una cita (solo administrador)
     * @param idCita ID de la cita a editar
     * @param dto DTO con la información actualizada
     * @return Cita actualizada
     */
    @PutMapping("/editar/{idCita}")
    public ResponseEntity<Cita> editarCitaAdmin(
            @PathVariable Long idCita,
            @RequestBody EditarCitaAdminDTO dto) {
        return ResponseEntity.ok(serviciosCitas.editarCitaAdmin(idCita, dto));
    }

    /**
     * Edita una cita (paciente)
     * @param idCita ID de la cita a editar
     * @param dto DTO con la información actualizada
     * @return Cita actualizada
     */
    @PutMapping("/paciente/editar/{idCita}")
    public ResponseEntity<Cita> editarCitaPaciente(
            @PathVariable Long idCita,
            @RequestBody EditarCitaPacienteDTO dto) {
        return ResponseEntity.ok(serviciosCitas.editarCitaPaciente(idCita, dto));
    }

    /**
     * Cancela una cita
     * @param idCita ID de la cita a cancelar
     * @return Mensaje de confirmación
     */
    @PutMapping("/cancelar/{idCita}")
    public ResponseEntity<String> cancelarCita(@PathVariable Long idCita) {
        serviciosCitas.cancelarCita(idCita);
        return ResponseEntity.ok("Cita cancelada exitosamente");
    }

    /**
     * Confirma una cita
     * @param idCita ID de la cita a confirmar
     * @return Mensaje de confirmación
     */
    @PutMapping("/confirmar/{idCita}")
    public ResponseEntity<String> confirmarCita(@PathVariable Long idCita) {
        serviciosCitas.confirmarCita(idCita);
        return ResponseEntity.ok("Cita confirmada exitosamente");
    }

    /**
     * Marca una cita como completada
     * @param idCita ID de la cita a marcar como completada
     * @return Mensaje de confirmación
     */
    @PutMapping("/completar/{idCita}")
    public ResponseEntity<String> completarCita(@PathVariable Long idCita) {
        serviciosCitas.completarCita(idCita);
        return ResponseEntity.ok("Cita marcada como completada exitosamente");
    }
} 