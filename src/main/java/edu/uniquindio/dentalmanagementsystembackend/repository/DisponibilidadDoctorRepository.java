package edu.uniquindio.dentalmanagementsystembackend.repository;

import edu.uniquindio.dentalmanagementsystembackend.entity.DisponibilidadDoctor;
import edu.uniquindio.dentalmanagementsystembackend.Enum.EstadoDisponibilidad;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;


@Repository
public interface DisponibilidadDoctorRepository extends JpaRepository<DisponibilidadDoctor, Long> {

    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END FROM DisponibilidadDoctor d " +
            "WHERE d.doctor.idNumber = :doctorId " +
            "AND d.diaSemana = :diaSemana " +
            "AND d.horaInicio <= :hora " +
            "AND d.horaFin > :hora " +
            "AND d.estado = :estado")
    boolean existsByDoctor_IdNumberAndFecha(
            @Param("doctorId") String doctorId,
            @Param("diaSemana") DayOfWeek diaSemana,
            @Param("hora") LocalTime hora,
            @Param("estado") EstadoDisponibilidad estado
    );

    List<DisponibilidadDoctor> findByDoctor_IdNumberAndDiaSemanaAndEstado(
            String idDoctor,
            DayOfWeek diaSemana,
            EstadoDisponibilidad estado
    );

    /**
     * Busca la disponibilidad para múltiples doctores en un día específico de la semana.
     * Este método optimiza la consulta para obtener las disponibilidades de varios doctores
     * en una sola llamada a la base de datos, mejorando el rendimiento cuando se necesitan
     * consultar múltiples doctores.
     *
     * @param doctorIds Colección de IDs de doctores
     * @param diaSemana Día de la semana a consultar
     * @param estado Estado de la disponibilidad (ACTIVO, INACTIVO, etc.)
     * @return Lista de disponibilidades que cumplen con los criterios
     */
    List<DisponibilidadDoctor> findByDoctor_IdNumberInAndDiaSemanaAndEstado(
            Collection<String> doctorIds,
            DayOfWeek diaSemana,
            EstadoDisponibilidad estado
    );
}
