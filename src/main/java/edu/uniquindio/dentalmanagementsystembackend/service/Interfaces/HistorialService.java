package edu.uniquindio.dentalmanagementsystembackend.service.Interfaces;

import edu.uniquindio.dentalmanagementsystembackend.dto.historial.CrearHistorialDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.historial.HistorialDTO;
import edu.uniquindio.dentalmanagementsystembackend.entity.Account.HistorialMedico;

import java.time.LocalDate;
import java.util.List;

public interface HistorialService {

    /**
     * Crea un nuevo registro en el historial médico.
     * @param dto DTO con la información del historial médico
     * @return HistorialMedico creado
     */
    HistorialMedico crearHistorial(CrearHistorialDTO dto);

    /**
     * Obtiene el historial médico de un paciente.
     * @param pacienteId ID del paciente
     * @return Lista de registros del historial médico
     */
    List<HistorialMedico> obtenerHistorialPorPaciente(Long pacienteId);

    /**
     * Obtiene el historial médico de un paciente en formato DTO.
     * @param pacienteId ID del paciente
     * @return Lista de DTOs del historial médico
     */
    List<HistorialDTO> obtenerHistorialesDTOPorPaciente(Long pacienteId);

    /**
     * Obtiene un historial médico específico por su ID.
     * @param historialId ID del historial médico
     * @return DTO del historial médico
     */
    HistorialDTO obtenerHistorialPorId(Long historialId);

    /**
     * Obtiene todos los historiales médicos de una fecha específica.
     * @param fecha Fecha a buscar
     * @return Lista de DTOs del historial médico
     */
    List<HistorialDTO> obtenerHistorialesPorFecha(LocalDate fecha);

    /**
     * Obtiene todos los historiales médicos de un odontólogo.
     * @param odontologoId ID del odontólogo
     * @return Lista de DTOs del historial médico
     */
    List<HistorialDTO> obtenerHistorialesPorOdontologo(Long odontologoId);

    /**
     * Obtiene todos los registros del historial médico.
     * @return Lista de todos los registros
     */
    List<HistorialMedico> obtenerTodosLosHistoriales();
} 