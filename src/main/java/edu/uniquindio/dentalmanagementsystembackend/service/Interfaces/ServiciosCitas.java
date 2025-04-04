package edu.uniquindio.dentalmanagementsystembackend.service.Interfaces;


import edu.uniquindio.dentalmanagementsystembackend.dto.cita.CrearCitaDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.historial.CrearHistorialDTO;
import edu.uniquindio.dentalmanagementsystembackend.entity.Account.HistorialMedico;
import edu.uniquindio.dentalmanagementsystembackend.entity.Cita;

public interface ServiciosCitas {

    /**
     * Crea un nuevo registro en el historial médico.
     * @param dto DTO con la información del historial médico
     * @return HistorialMedico creado
     */
    Cita crearCita(CrearCitaDTO dto);



}
