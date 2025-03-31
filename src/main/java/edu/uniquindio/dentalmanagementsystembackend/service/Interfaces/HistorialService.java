package edu.uniquindio.dentalmanagementsystembackend.service.Interfaces;

import edu.uniquindio.dentalmanagementsystembackend.entity.HistorialMedico;
import java.time.LocalDate;
import java.util.List;

public interface HistorialService {
    
    /**
     * Crea un nuevo registro en el historial médico.
     * @param pacienteId ID del paciente
     * @param odontologoId ID del odontólogo
     * @param fecha Fecha del registro
     * @param diagnostico Diagnóstico del paciente
     * @param tratamiento Tratamiento realizado
     * @param observaciones Observaciones adicionales
     * @param proximaCita Fecha de la próxima cita
     * @return HistorialMedico creado
     */
    HistorialMedico crearHistorial(Long pacienteId, Long odontologoId, LocalDate fecha, 
                                  String diagnostico, String tratamiento, String observaciones, 
                                  LocalDate proximaCita);

    /**
     * Obtiene el historial médico de un paciente.
     * @param pacienteId ID del paciente
     * @return Lista de registros del historial médico
     */
    List<HistorialMedico> obtenerHistorialPorPaciente(Long pacienteId);

    /**
     * Obtiene todos los registros del historial médico.
     * @return Lista de todos los registros
     */
    List<HistorialMedico> obtenerTodosLosHistoriales();
} 