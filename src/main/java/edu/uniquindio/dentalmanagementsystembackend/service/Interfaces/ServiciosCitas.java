package edu.uniquindio.dentalmanagementsystembackend.service.Interfaces;


import edu.uniquindio.dentalmanagementsystembackend.dto.cita.CrearCitaDTO;
import edu.uniquindio.dentalmanagementsystembackend.entity.Cita;
import edu.uniquindio.dentalmanagementsystembackend.entity.Account.User;
import edu.uniquindio.dentalmanagementsystembackend.entity.Especialidad;

import java.util.List;

public interface ServiciosCitas {

    /**
     * Crea un nuevo registro en el historial médico.
     * @param dto DTO con la información del historial médico
     * @return HistorialMedico creado
     */
    Cita crearCita(CrearCitaDTO dto);
    
    /**
     * Obtiene los doctores disponibles para una especialidad específica
     * @param especialidadId ID de la especialidad
     * @return Lista de doctores con la especialidad especificada
     */
    List<User> obtenerDoctoresPorEspecialidad(Long especialidadId);



}
