package edu.uniquindio.dentalmanagementsystembackend.Citas;

// Importa la enumeración TipoCita desde el paquete Enum
import edu.uniquindio.dentalmanagementsystembackend.Enum.EstadoCitas;
import edu.uniquindio.dentalmanagementsystembackend.Enum.TipoCita;

// Importa la clase ListaCitasDTO desde el paquete dto
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.ListaCitasDTO;
// Importa la interfaz CitasRepository desde el paquete repository
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.CitaDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.DoctorDisponibilidadDTO;
import edu.uniquindio.dentalmanagementsystembackend.entity.Cita;
import edu.uniquindio.dentalmanagementsystembackend.repository.CitasRepository;
// Importa la interfaz ServiciosCitas desde el paquete service. Interfaces
import edu.uniquindio.dentalmanagementsystembackend.service.Interfaces.ServiciosCitas;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
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

    // Prueba unitaria para listar citas de un paciente específico
    @Test
    void testListarCitasPaciente()throws  Exception{
        Long idPaciente= 222000222L;

        // Imprime las citas obtenidas para el paciente con el ID especificado
        System.out.println(serviciosCitas.obtenerCitasPorPaciente(idPaciente));
    }

    // Prueba unitaria para obtener todas las citas
    @Test
    void testObtenerTodasLasCitas() {
        // Obtiene todas las citas y las almacena en una lista
        List<ListaCitasDTO> citas = serviciosCitas.obtenerTodasLasCitas();
        // Imprime el número de citas encontradas
        System.out.println("Citas encontradas: " + citas.size());
        // Imprime cada cita en la lista
        citas.forEach(System.out::println);
    }

    // Prueba unitaria para editar una cita específica
    @Test
    void testEditarCita() {
        Long idCita = 2L; // Asegúrate de que exista en la BD
        TipoCita nuevoTipoCita = TipoCita.ORTODONCIA;
        // Edita la cita con el ID y el nuevo tipo de cita especificados
        serviciosCitas.editarCita(idCita, nuevoTipoCita);
        // Imprime un mensaje indicando que la cita fue editada correctamente
        System.out.println("Cita con ID " + idCita + " editada correctamente.");
    }

    // Prueba unitaria para cancelar una cita específica
    @Test
    void testCancelarCita() {
        Long idCita = 42L; // Asegúrate de que exista en la BD
        // Cancela la cita con el ID especificado
        serviciosCitas.cancelarCita(idCita);
        // Imprime un mensaje indicando que la cita fue cancelada correctamente
        System.out.println("Cita con ID " + idCita + " cancelada correctamente.");
    }

 @Test
 void testCrearCita() throws Exception {
     Long idPaciente = 222000222L;
     Long idDoctor = 222222222L; // Example doctor ID
     LocalDateTime fechaHora = LocalDateTime.of(2025, 8, 15, 10, 0); // Example date and time

     CitaDTO cita = new CitaDTO(idPaciente, EstadoCitas.CONFIRMADA, TipoCita.CONSULTA_GENERAL, idDoctor, fechaHora);
     serviciosCitas.crearCita(cita);
 }

    @Test
    void testObtenerFechasDisponiblesDoctores() {
        // Obtiene las fechas más cercanas disponibles de todos los doctores
        List<DoctorDisponibilidadDTO> fechasDisponibles = serviciosCitas.obtenerFechasDisponiblesDoctores();
        // Imprime el número de doctores encontrados
        System.out.println("Doctores encontrados: " + fechasDisponibles.size());
        // Imprime cada doctor y sus fechas disponibles
        fechasDisponibles.forEach(System.out::println);
    }
}