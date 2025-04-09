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


} 