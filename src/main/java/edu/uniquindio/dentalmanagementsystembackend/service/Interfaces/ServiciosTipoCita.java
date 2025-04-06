package edu.uniquindio.dentalmanagementsystembackend.service.Interfaces;

import edu.uniquindio.dentalmanagementsystembackend.dto.cita.TipoCitaDTO;
import edu.uniquindio.dentalmanagementsystembackend.entity.TipoCita;

import java.util.List;


public interface ServiciosTipoCita {
    
    /**
     * Obtiene todos los tipos de cita disponibles
     * @return Lista de tipos de cita
     */
    List<TipoCitaDTO> listarTiposCita();
} 