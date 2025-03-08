package edu.uniquindio.dentalmanagementsystembackend.repository;

import edu.uniquindio.dentalmanagementsystembackend.entity.Account.User;
import edu.uniquindio.dentalmanagementsystembackend.entity.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CitasRepository  extends JpaRepository <Cita,Long>{

    @Query("SELECT c FROM Cita c WHERE c.paciente.idNumber = :pacienteId")
    List<Cita> findByPacienteId(@Param("pacienteId") String pacienteId);

    @Query("SELECT c FROM Cita c WHERE c.paciente = :paciente AND c.fechaHora = :fechaHora")
    Optional<Cita> findByPacienteAndFechaHora(@Param("paciente") User paciente, @Param("fechaHora") LocalDateTime fechaHora);

    @Query("SELECT c FROM Cita c WHERE c.odontologo = :odontologo AND FUNCTION('DATE', c.fechaHora) = :fecha")
    List<Cita> findByOdontologoAndFecha(@Param("odontologo") User odontologo, @Param("fecha") LocalDate fecha);




}
