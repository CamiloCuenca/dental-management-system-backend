package edu.uniquindio.dentalmanagementsystembackend.service.Interfaces;

import edu.uniquindio.dentalmanagementsystembackend.dto.cita.CrearCitaDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.DoctorEspecialidadDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.EditarCitaAdminDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.EditarCitaPacienteDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.FechaDisponibleDTO;
import edu.uniquindio.dentalmanagementsystembackend.entity.Cita;
import edu.uniquindio.dentalmanagementsystembackend.entity.Account.User;
import edu.uniquindio.dentalmanagementsystembackend.entity.Especialidad;

import java.time.LocalDate;
import java.util.List;

public interface ServiciosCitas {

    /**
     * Crea una nueva cita
     * @param dto DTO con la información de la cita
     * @return Cita creada
     */
    Cita crearCita(CrearCitaDTO dto);

    /**
     * Obtiene los doctores disponibles para una especialidad específica
     * @param especialidadId ID de la especialidad
     * @return Lista de doctores con la especialidad especificada
     */
    List<DoctorEspecialidadDTO> obtenerDoctoresPorEspecialidad(Long especialidadId);

    /**
     * Obtiene todas las citas de un paciente
     * @param idPaciente ID del paciente
     * @return Lista de citas del paciente
     */
    List<Cita> obtenerCitasPorPaciente(String idPaciente);

    /**
     * Obtiene todas las citas de un doctor
     * @param idDoctor ID del doctor
     * @return Lista de citas del doctor
     */
    List<Cita> obtenerCitasPorDoctor(String idDoctor);

    /**
     * Edita una cita (solo administrador)
     * @param idCita ID de la cita a editar
     * @param dto DTO con la información actualizada
     * @return Cita actualizada
     */
    Cita editarCitaAdmin(Long idCita, EditarCitaAdminDTO dto);

    /**
     * Edita una cita (solo paciente)
     * @param idCita ID de la cita a editar
     * @param dto DTO con la información actualizada
     * @return Cita actualizada
     */
    Cita editarCitaPaciente(Long idCita, EditarCitaPacienteDTO dto);

    /**
     * Cancela una cita
     * @param idCita ID de la cita a cancelar
     */
    void cancelarCita(Long idCita);

    /**
     * Confirma una cita
     * @param idCita ID de la cita a confirmar
     */
    void confirmarCita(Long idCita);

    /**
     * Marca una cita como completada
     * @param idCita ID de la cita a completar
     */
    void completarCita(Long idCita);

    /**
     * Obtiene las fechas disponibles para un doctor en un rango de fechas
     * @param doctorId ID del doctor
     * @param fechaInicio Fecha de inicio del rango
     * @param fechaFin Fecha de fin del rango
     * @return Lista de fechas disponibles con sus horarios
     */
    List<FechaDisponibleDTO> obtenerFechasDisponibles(String doctorId, LocalDate fechaInicio, LocalDate fechaFin);
}
