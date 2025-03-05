package edu.uniquindio.dentalmanagementsystembackend.repository;

import edu.uniquindio.dentalmanagementsystembackend.entity.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CitasRepository  extends JpaRepository <Cita,Long>{

    @Query("SELECT c FROM Cita c WHERE c.paciente.idNumber = :pacienteId")
    List<Cita> findByPacienteId(@Param("pacienteId") String pacienteId);}
