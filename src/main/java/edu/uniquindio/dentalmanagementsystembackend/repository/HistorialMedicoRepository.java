package edu.uniquindio.dentalmanagementsystembackend.repository;

import edu.uniquindio.dentalmanagementsystembackend.entity.HistorialMedico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistorialMedicoRepository extends JpaRepository<HistorialMedico, Long> {
    
    @Query("SELECT h FROM HistorialMedico h WHERE h.paciente.idNumber = :pacienteId")
    List<HistorialMedico> findByPacienteIdNumber(@Param("pacienteId") String pacienteId);
} 