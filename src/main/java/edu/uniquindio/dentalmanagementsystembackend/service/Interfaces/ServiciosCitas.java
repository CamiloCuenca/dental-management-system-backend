package edu.uniquindio.dentalmanagementsystembackend.service.Interfaces;

import edu.uniquindio.dentalmanagementsystembackend.dto.cita.CrearCitaDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.CrearCitaNoAutenticadaDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.DoctorEspecialidadDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.EditarCitaAdminDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.EditarCitaPacienteDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.FechaDisponibleDTO;
import edu.uniquindio.dentalmanagementsystembackend.Enum.EstadoCitas;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.CitaDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.EditarCitaNoAutenticadaAdminDTO;
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
     * Crea una nueva cita no autenticada
     * @param dto DTO con la información de la cita
     * @return Cita creada
     */
    Cita crearCitaNoAutenticada(CrearCitaNoAutenticadaDTO dto);

    /**
     * Obtiene los doctores disponibles para una especialidad específica
     * @param especialidadId ID de la especialidad
     * @return Lista de doctores con la especialidad especificada
     */
    List<DoctorEspecialidadDTO> obtenerDoctoresPorEspecialidad(Long especialidadId);

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
     * Obtiene todas las citas de un paciente
     * @param idPaciente ID del paciente
     * @return Lista de citas del paciente
     */
    List<CitaDTO> obtenerCitasPorPaciente(String idPaciente);

    /**
     * Obtiene todas las citas de un doctor
     * @param idDoctor ID del doctor
     * @return Lista de citas del doctor
     */
    List<CitaDTO> obtenerCitasPorDoctor(String idDoctor);



    /**
     * Obtiene las fechas disponibles para un doctor en un rango de fechas
     * @param doctorId ID del doctor
     * @param fechaInicio Fecha de inicio del rango
     * @param fechaFin Fecha de fin del rango
     * @return Lista de fechas disponibles con sus horarios
     */
    List<FechaDisponibleDTO> obtenerFechasDisponibles(String doctorId, LocalDate fechaInicio, LocalDate fechaFin);

    /**
     * Edita una cita no autenticada como administrador
     * @param idCita ID de la cita a editar
     * @param dto DTO con los nuevos datos de la cita
     * @return La cita editada
     */
    Cita editarCitaNoAutenticadaAdmin(Long idCita, EditarCitaNoAutenticadaAdminDTO dto);

    /**
     * Cancela una cita no autenticada como administrador
     * @param idCita ID de la cita a cancelar
     */
    void cancelarCitaNoAutenticadaAdmin(Long idCita);

    /**
     * Cambia el estado de una cita no autenticada como administrador
     * @param idCita ID de la cita
     * @param nuevoEstado Nuevo estado de la cita
     */
    void cambiarEstadoCitaNoAutenticadaAdmin(Long idCita, EstadoCitas nuevoEstado);
}
