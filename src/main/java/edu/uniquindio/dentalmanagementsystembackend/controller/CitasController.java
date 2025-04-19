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
import edu.uniquindio.dentalmanagementsystembackend.repository.CitasRepository;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/citas")
@RequiredArgsConstructor
public class CitasController {

    private final ServiciosCitas serviciosCitas;
    private final ServiciosTipoCita serviciosTipoCita;
    private final ServiciosDisponibilidadDoctor serviciosDisponibilidadDoctor;
    private final CitasRepository citasRepository;
    
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

    /**
     * Obtiene todas las citas de un paciente
     * @param idPaciente ID del paciente
     * @return Lista de citas del paciente
     */
    @GetMapping("/paciente/{idPaciente}")
    public ResponseEntity<List<CitaDTO>> obtenerCitasPorPaciente(@PathVariable String idPaciente) {
        // Validar parámetros de entrada
        if (idPaciente == null || idPaciente.trim().isEmpty()) {
            System.out.println("ID de paciente inválido: " + idPaciente);
            return ResponseEntity.badRequest().body(List.of());
        }

        try {
            // Obtener citas usando el repositorio
            List<Cita> citas = citasRepository.findByPaciente_IdNumber(idPaciente);
            System.out.println("Se encontraron " + citas.size() + " citas para el paciente con ID " + idPaciente);

            // Transformar a DTOs
            List<CitaDTO> citasDTO = citas.stream()
                    .map(cita -> new CitaDTO(
                            cita.getId(),
                            cita.getPaciente() != null ? cita.getPaciente().getIdNumber() : null,
                            cita.getPaciente() != null ? cita.getPaciente().getName() : null,
                            cita.getDoctor() != null ? cita.getDoctor().getIdNumber() : null,
                            cita.getDoctor() != null ? cita.getDoctor().getName() : null,
                            cita.getFechaHora(),
                            cita.getEstado(),
                            cita.getPaciente() != null ? cita.getPaciente().getAccount().getEmail() : null,
                            cita.getPaciente() != null ? cita.getPaciente().getPhoneNumber() : null,
                            cita.getTipoCita().getId(),
                            cita.getTipoCita().getNombre(),
                            cita.getTipoCita().getDuracionMinutos()
                    ))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(citasDTO);
        } catch (Exception e) {
            System.out.println("Error al obtener citas para el paciente " + idPaciente + ": " + e.getMessage());
            e.printStackTrace(); // Para depuración

            return ResponseEntity
                    .status(500)
                    .header("X-Error", "Error procesando la solicitud: " + e.getMessage())
                    .body(List.of());
        }
    }


    /**
     * Obtiene todas las citas de un doctor
     * @param idDoctor ID del doctor
     * @return Lista de citas del doctor
     */
    @GetMapping("/doctor/{idDoctor}")
    public List<CitaDTO> obtenerCitasPorDoctor(@PathVariable String idDoctor) {
        // Validar parámetros de entrada
        if (idDoctor == null || idDoctor.trim().isEmpty()) {
            System.out.println("ID de doctor inválido: " + idDoctor);
            throw new IllegalArgumentException("El ID del doctor no puede estar vacío");
        }

        // Buscar explícitamente solo citas autenticadas
        List<Cita> citas = citasRepository.findByDoctor_IdNumberAndEsAutenticadaTrue(idDoctor);
        System.out.println("Se encontraron " + citas.size() + " citas autenticadas para el doctor " + idDoctor);

        // Transformar a DTOs y retornar
        return citas.stream()
                .map(cita -> new CitaDTO(
                        cita.getId(),
                        cita.getPaciente() != null ? cita.getPaciente().getIdNumber() : null,
                        cita.getPaciente() != null ? cita.getPaciente().getName() : cita.getNombrePacienteNoAutenticado(),
                        cita.getDoctor() != null ? cita.getDoctor().getIdNumber() : null,
                        cita.getDoctor() != null ? cita.getDoctor().getName() : null,
                        cita.getFechaHora(),
                        cita.getEstado(),
                        cita.getPaciente() != null ? cita.getPaciente().getAccount().getEmail() : cita.getEmailNoAutenticado(),
                        cita.getPaciente() != null ? cita.getPaciente().getPhoneNumber() : cita.getTelefonoNoAutenticado(),
                        cita.getTipoCita().getId(),
                        cita.getTipoCita().getNombre(),
                        cita.getTipoCita().getDuracionMinutos()
                ))
                .collect(Collectors.toList());
    }





    /**
     * Obtiene todas las citas no autenticadas de un doctor específico.
     *
     * @param idDoctor El identificador único del doctor.
     * @return Lista de citas no autenticadas del doctor.
     */
    @GetMapping("/doctor-no-autenticadas/{idDoctor}")
    public ResponseEntity<List<CitaDTO>> obtenerCitasNoAutenticadasPorDoctor(@PathVariable String idDoctor) {
        // Validar parámetros de entrada
        if (idDoctor == null || idDoctor.trim().isEmpty()) {
            System.out.println("ID de doctor inválido: " + idDoctor);
            return ResponseEntity.badRequest().body(List.of());
        }

        try {
            // Usar el nuevo método de repositorio para obtener solo citas no autenticadas válidas
            List<Cita> citas = citasRepository.findValidNoAuthCitasByDoctorId(idDoctor);
            System.out.println("Se encontraron " + citas.size() + " citas no autenticadas válidas para el doctor " + idDoctor);

            // Mapeo mejorado incluyendo el número de identificación
            List<CitaDTO> citasDTO = citas.stream()
                    .map(cita -> new CitaDTO(
                            cita.getId(),
                            cita.getNumeroIdentificacionNoAutenticado(), // Usamos esto como ID para citas no autenticadas
                            cita.getNombrePacienteNoAutenticado(),
                            cita.getDoctor() != null ? cita.getDoctor().getIdNumber() : null,
                            cita.getDoctor() != null ? cita.getDoctor().getName() : null,
                            cita.getFechaHora(),
                            cita.getEstado(),
                            cita.getEmailNoAutenticado(),
                            cita.getTelefonoNoAutenticado(),
                            cita.getTipoCita().getId(),
                            cita.getTipoCita().getNombre(),
                            cita.getTipoCita().getDuracionMinutos()
                    ))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(citasDTO);
        } catch (Exception e) {
            System.out.println("Error al obtener citas no autenticadas: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(List.of());
        }
    }





    /**
     * Obtiene todas las citas no autenticadas de un paciente específico.
     *
     * @param idPaciente El identificador único del paciente.
     * @return Lista de citas no autenticadas del paciente.
     */
    @GetMapping("/paciente-no-autenticadas/{idPaciente}")
    public ResponseEntity<List<CitaDTO>> obtenerCitasNoAutenticadasPorPaciente(@PathVariable String idPaciente) {
        // Validar parámetros de entrada
        if (idPaciente == null || idPaciente.trim().isEmpty()) {
            System.out.println("ID de paciente inválido: " + idPaciente);
            return ResponseEntity.badRequest().body(List.of());
        }

        try {
            // Obtener citas no autenticadas usando el repositorio
            List<Cita> citas = citasRepository.findByNumeroIdentificacionNoAutenticadoAndEsAutenticadaFalse(idPaciente);
            System.out.println("Se encontraron " + citas.size() + " citas no autenticadas para el paciente con ID " + idPaciente);

            // Transformar a DTOs
            List<CitaDTO> citasDTO = citas.stream()
                    .map(cita -> new CitaDTO(
                            cita.getId(),
                            null, // pacienteId es null para citas no autenticadas
                            cita.getNombrePacienteNoAutenticado(),
                            cita.getDoctor() != null ? cita.getDoctor().getIdNumber() : null,
                            cita.getDoctor() != null ? cita.getDoctor().getName() : null,
                            cita.getFechaHora(),
                            cita.getEstado(),
                            cita.getEmailNoAutenticado(),
                            cita.getTelefonoNoAutenticado(),
                            cita.getTipoCita().getId(),
                            cita.getTipoCita().getNombre(),
                            cita.getTipoCita().getDuracionMinutos()
                    ))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(citasDTO);
        } catch (Exception e) {
            System.out.println("Error al obtener citas no autenticadas para el paciente " + idPaciente + ": " + e.getMessage());
            e.printStackTrace(); // Para depuración

            // Devolver un mensaje más explicativo en el cuerpo de la respuesta
            return ResponseEntity
                    .status(500)
                    .header("X-Error", "Error procesando la solicitud: " + e.getMessage())
                    .body(List.of());
        }
    }




} 