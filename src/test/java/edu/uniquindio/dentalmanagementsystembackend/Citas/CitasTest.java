package edu.uniquindio.dentalmanagementsystembackend.Citas;

// Importa la enumeración TipoCita desde el paquete Enum

import edu.uniquindio.dentalmanagementsystembackend.Enum.EstadoCitas;
import edu.uniquindio.dentalmanagementsystembackend.Enum.TipoCita;

// Importa la clase ListaCitasDTO desde el paquete dto
// Importa la interfaz CitasRepository desde el paquete repository
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.CrearCitaDTO;
import edu.uniquindio.dentalmanagementsystembackend.repository.CitasRepository;
// Importa la interfaz ServiciosCitas desde el paquete service. Interfaces
import edu.uniquindio.dentalmanagementsystembackend.service.Interfaces.ServiciosCitas;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

// Anotación que indica que esta clase es una prueba de Spring Boot
@SpringBootTest
public class CitasTest {

    // Inyección de dependencias para el repositorio de citas
    @Autowired
    private CitasRepository citasRepository;

    // Inyección de dependencias para el servicio de citas
    @Autowired
    ServiciosCitas serviciosCitas;


    @Test
    void crarCitaTest() {
        Long idPaciente = 1001277430L;
        Long idOdontologo = 111111111L;

        // Crear fecha específica: 15 de mayo de 2025 a las 9:30 AM en zona horaria local (o Bogotá)
        ZonedDateTime fechaDeseada = ZonedDateTime.of(
                2025, 5, 15, 9, 30, 0, 0, ZoneId.of("America/Bogota")
        );

        Instant fechaHora = fechaDeseada.toInstant();

        CrearCitaDTO crearCitaDTO = new CrearCitaDTO(
                idPaciente,
                idOdontologo,
                fechaHora,
                TipoCita.CONSULTA_GENERAL
        );

        serviciosCitas.crearCita(crearCitaDTO);
    }


}