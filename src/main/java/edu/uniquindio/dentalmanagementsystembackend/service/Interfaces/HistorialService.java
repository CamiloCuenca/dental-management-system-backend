package edu.uniquindio.dentalmanagementsystembackend.service.Interfaces;

import edu.uniquindio.dentalmanagementsystembackend.dto.historial.ActualizarHistorial;
import edu.uniquindio.dentalmanagementsystembackend.dto.historial.CrearHistorialDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.historial.HistorialDTO;
import edu.uniquindio.dentalmanagementsystembackend.entity.Account.HistorialMedico;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface HistorialService {

    /**
     * Crea un nuevo registro en el historial médico.
     * @param dto DTO con la información del historial médico
     * @return HistorialMedico creado
     */
    HistorialMedico crearHistorial(CrearHistorialDTO dto);

    /**
     * Retorna los historiales médicos agrupados por año para un paciente específico.
     * @param idPaciente ID del paciente
     * @return Mapa con el año como clave y la lista de historiales de ese año como valor
     */
    Map<Integer, List<HistorialDTO>> listarHistorialesPorPacienteAgrupadosPorAnio(String idPaciente);

    //Crea un servicio paresido al anteiror pero que devuelve el de un año en especifico
    /**
     * Retorna los historiales médicos de un paciente específico para un año dado.
     * @param idPaciente ID del paciente
     * @param anio Año para filtrar los historiales
     * @return Lista de historiales médicos del paciente para el año especificado
     */
    List<HistorialDTO> listarHistorialesPorPacienteYAnio(String idPaciente, int anio);

    /**
     * Actualiza un historial médico específico por su ID.
     * @param id
     */
    void ActualizarHistorial (Long id, ActualizarHistorial nuevoHistorial);

    /**
     * Elimina un historial médico específico por su ID.
     * @param id
     */
    void eliminarHistorial(Long id);
} 