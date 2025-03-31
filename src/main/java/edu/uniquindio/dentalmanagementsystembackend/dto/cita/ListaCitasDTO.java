package edu.uniquindio.dentalmanagementsystembackend.dto.cita;

import edu.uniquindio.dentalmanagementsystembackend.Enum.EstadoCitas;
import edu.uniquindio.dentalmanagementsystembackend.Enum.TipoCita;
import edu.uniquindio.dentalmanagementsystembackend.exception.InvalidDataException;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;

public record ListaCitasDTO(
        @NotNull(message = "El ID de la cita no puede ser nulo")
        @Positive(message = "El ID de la cita debe ser positivo")
        Integer idCita,
        
        @NotNull(message = "El ID del paciente no puede ser nulo")
        @Positive(message = "El ID del paciente debe ser positivo")
        Long idPaciente,
        
        @NotNull(message = "El ID del doctor no puede ser nulo")
        @Positive(message = "El ID del doctor debe ser positivo")
        Long idDoctor,
        
        @NotNull(message = "La fecha y hora no pueden ser nulas")
        LocalDateTime fechaHora,
        
        @NotNull(message = "El estado de la cita no puede ser nulo")
        EstadoCitas estado,
        
        @NotNull(message = "El tipo de cita no puede ser nulo")
        TipoCita tipoCita
) {
    /**
     * Valida los datos de la cita para la lista.
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
        if (idCita <= 0) {
            throw new InvalidDataException("El ID de la cita debe ser mayor a 0");
        }
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
        if (fechaHora == null) {
            throw new InvalidDataException("La fecha y hora no pueden ser nulas");
        }
        
        // No validamos que sea futura porque este DTO es para mostrar citas existentes
        if (fechaHora.getHour() < 0 || fechaHora.getHour() > 23) {
            throw new InvalidDataException("La hora debe estar entre 0 y 23");
        }
        if (fechaHora.getDayOfWeek().getValue() < 1 || fechaHora.getDayOfWeek().getValue() > 7) {
            throw new InvalidDataException("El día de la semana debe estar entre 1 y 7");
        }
    }
}
