package edu.uniquindio.dentalmanagementsystembackend.repository;

import edu.uniquindio.dentalmanagementsystembackend.entity.DisponibilidadDoctor;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface DisponibilidadDoctorRepository extends JpaRepository<DisponibilidadDoctor, Long> {

    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END FROM DisponibilidadDoctor d " +
           "WHERE d.doctor.idNumber = :doctorId " +
           "AND d.diaSemana = :diaSemana " +
           "AND d.horaInicio <= :hora " +
           "AND d.horaFin > :hora " +
           "AND d.estado = 'ACTIVO'")
    boolean existsByDoctor_IdNumberAndFecha(
        @Param("doctorId") String doctorId,
        @Param("diaSemana") DayOfWeek diaSemana,
        @Param("hora") LocalTime hora
    );

    List<DisponibilidadDoctor> findByDoctor_IdNumberAndDiaSemanaAndEstado(String idDoctor, DayOfWeek diaSemana, String estado);

    // Métodos personalizados si son necesarios
} 