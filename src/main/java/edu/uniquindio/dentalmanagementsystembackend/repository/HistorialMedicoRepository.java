package edu.uniquindio.dentalmanagementsystembackend.repository;

import edu.uniquindio.dentalmanagementsystembackend.entity.Account.HistorialMedico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistorialMedicoRepository extends JpaRepository<HistorialMedico, Long> {
    
    /**
     * Busca todos los historiales médicos de un paciente por su ID
     * @param pacienteId ID del paciente
     * @return Lista de historiales médicos del paciente ordenados por fecha descendente
     */
    @Query("SELECT h FROM HistorialMedico h WHERE h.paciente.idNumber = :pacienteId ORDER BY h.fecha DESC")
    List<HistorialMedico> findByPacienteIdNumber(@Param("pacienteId") String pacienteId);
} 