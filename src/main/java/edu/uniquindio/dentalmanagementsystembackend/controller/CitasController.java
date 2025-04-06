package edu.uniquindio.dentalmanagementsystembackend.controller;

import edu.uniquindio.dentalmanagementsystembackend.dto.cita.CrearCitaDTO;
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
    @PostMapping
    public ResponseEntity<Cita> crearCita(@RequestBody CrearCitaDTO dto) {
        return ResponseEntity.ok(serviciosCitas.crearCita(dto));
    }
    
    /**
     * Obtiene todos los tipos de cita disponibles
     * @return Lista de tipos de cita
     */
    @GetMapping("/tipos")
    public ResponseEntity<List<TipoCita>> obtenerTiposCita() {
        return ResponseEntity.ok(serviciosTipoCita.listarTiposCita());
    }
    
    /**
     * Obtiene los doctores disponibles para una especialidad específica
     * @param especialidadId ID de la especialidad
     * @return Lista de doctores con la especialidad especificada
     */
    @GetMapping("/doctores/{especialidadId}")
    public ResponseEntity<List<User>> obtenerDoctoresPorEspecialidad(@PathVariable Long especialidadId) {
        return ResponseEntity.ok(serviciosCitas.obtenerDoctoresPorEspecialidad(especialidadId));
    }
    
    /**
     * Obtiene las fechas disponibles para un doctor en un rango de fechas
     * @param doctorId ID del doctor
     * @param fechaInicio Fecha de inicio del rango
     * @param fechaFin Fecha de fin del rango
     * @return Lista de fechas disponibles
     */
    @GetMapping("/disponibilidad/fechas/{doctorId}")
    public ResponseEntity<List<LocalDate>> obtenerFechasDisponibles(
            @PathVariable String doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        return ResponseEntity.ok(serviciosDisponibilidadDoctor.obtenerFechasDisponibles(doctorId, fechaInicio, fechaFin));
    }
    
    /**
     * Obtiene los horarios disponibles para un doctor en una fecha específica
     * @param doctorId ID del doctor
     * @param fecha Fecha para la que se quieren los horarios
     * @return Lista de horarios disponibles
     */
    @GetMapping("/disponibilidad/horarios/{doctorId}")
    public ResponseEntity<List<LocalTime>> obtenerHorariosDisponibles(
            @PathVariable String doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(serviciosDisponibilidadDoctor.obtenerHorariosDisponibles(doctorId, fecha));
    }
} 