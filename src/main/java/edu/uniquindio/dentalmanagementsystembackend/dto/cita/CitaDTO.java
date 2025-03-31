package edu.uniquindio.dentalmanagementsystembackend.dto.cita;

import edu.uniquindio.dentalmanagementsystembackend.Enum.EstadoCitas;
import edu.uniquindio.dentalmanagementsystembackend.Enum.TipoCita;
import edu.uniquindio.dentalmanagementsystembackend.exception.InvalidDataException;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Future;
import java.time.LocalDateTime;

public record CitaDTO(
        @NotNull(message = "El ID del paciente no puede ser nulo")
        Long idPaciente,
        
        @NotNull(message = "El estado de la cita no puede ser nulo")
        EstadoCitas estado,
        
        @NotNull(message = "El tipo de cita no puede ser nulo")
        TipoCita tipoCita,
        
        @NotNull(message = "El ID del doctor no puede ser nulo")
        Long idDoctor,
        
        @NotNull(message = "La fecha y hora no pueden ser nulas")
        @Future(message = "La fecha y hora de la cita deben ser futuras")
        LocalDateTime fechaHora
) {
    /**
     * Valida los datos de la cita.
     *
     * @throws InvalidDataException si los datos no son válidos
     */
    public void validar() throws InvalidDataException {
        validarIds();
        validarFechaHora();
    }

    /**
     * Valida que los IDs sean válidos.
     *
     * @throws InvalidDataException si los IDs no son válidos
     */
    private void validarIds() throws InvalidDataException {
        if (idPaciente <= 0) {
            throw new InvalidDataException("El ID del paciente debe ser mayor a 0");
        }
        if (idDoctor <= 0) {
            throw new InvalidDataException("El ID del doctor debe ser mayor a 0");
        }
        if (idPaciente.equals(idDoctor)) {
            throw new InvalidDataException("El paciente no puede ser el mismo que el doctor");
        }
    }

    /**
     * Valida que la fecha y hora sean válidas.
     *
     * @throws InvalidDataException si la fecha y hora no son válidas
     */
    private void validarFechaHora() throws InvalidDataException {
        LocalDateTime ahora = LocalDateTime.now();
        if (fechaHora.isBefore(ahora)) {
            throw new InvalidDataException("La fecha y hora de la cita deben ser futuras");
        }
        if (fechaHora.getHour() < 8 || fechaHora.getHour() > 18) {
            throw new InvalidDataException("Las citas solo pueden ser programadas entre las 8:00 y las 18:00");
        }
        if (fechaHora.getDayOfWeek().getValue() == 6 || fechaHora.getDayOfWeek().getValue() == 7) {
            throw new InvalidDataException("Las citas no pueden ser programadas en fin de semana");
        }
    }
}
