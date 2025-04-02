package edu.uniquindio.dentalmanagementsystembackend.service.Interfaces;

import edu.uniquindio.dentalmanagementsystembackend.dto.historial.CrearHistorialDTO;
import edu.uniquindio.dentalmanagementsystembackend.entity.Account.HistorialMedico;

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
    List<HistorialMedico> obtenerHistorialPorPaciente(String pacienteId);

    /**
     * Obtiene todos los registros del historial médico.
     * @return Lista de todos los registros
     */
    List<HistorialMedico> obtenerTodosLosHistoriales();
} 