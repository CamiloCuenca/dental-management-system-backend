package edu.uniquindio.dentalmanagementsystembackend.repository;

import edu.uniquindio.dentalmanagementsystembackend.entity.Account.User;
import edu.uniquindio.dentalmanagementsystembackend.entity.Cita;
import edu.uniquindio.dentalmanagementsystembackend.Enum.EstadoCitas;
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
public interface CitasRepository extends JpaRepository<Cita, Long> {

    List<Cita> findByPaciente_IdNumber(String idPaciente);
    List<Cita> findByDoctor_IdNumber(String idDoctor);
    List<Cita> findByFechaHoraBetween(Instant fechaInicio, Instant fechaFin);
    
    boolean existsByDoctorAndFechaHoraBetween(User doctor, Instant fechaInicio, Instant fechaFin);
}
