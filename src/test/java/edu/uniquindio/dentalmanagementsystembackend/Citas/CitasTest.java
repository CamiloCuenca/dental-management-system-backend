package edu.uniquindio.dentalmanagementsystembackend.Citas;

import edu.uniquindio.dentalmanagementsystembackend.Enum.TipoCita;
import edu.uniquindio.dentalmanagementsystembackend.dto.CitaDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.ListaCitasDTO;
import edu.uniquindio.dentalmanagementsystembackend.repository.CitasRepository;
import edu.uniquindio.dentalmanagementsystembackend.service.Interfaces.ServiciosCitas;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class CitasTest {

    @Autowired
    private CitasRepository citasRepository;

    @Autowired
    ServiciosCitas serviciosCitas;

    @Test
    void testListarCitasPaciente()throws  Exception{
        Long idPaciente= 555666777L;

        System.out.println(serviciosCitas.obtenerCitasPorPaciente(idPaciente));
    }

    @Test
    void testObtenerTodasLasCitas() {
        List<ListaCitasDTO> citas = serviciosCitas.obtenerTodasLasCitas();
        System.out.println("Citas encontradas: " + citas.size());
        citas.forEach(System.out::println);
    }

    @Test
    void testEditarCita() {
        Long idCita = 2L; // Asegúrate de que exista en la BD
        TipoCita nuevoTipoCita = TipoCita.ORTODONCIA;
        serviciosCitas.editarCita(idCita, nuevoTipoCita);
        System.out.println("Cita con ID " + idCita + " editada correctamente.");
    }

    @Test
    void testCancelarCita() {
        Long idCita = 2L; // Asegúrate de que exista en la BD
        serviciosCitas.cancelarCita(idCita);
        System.out.println("Cita con ID " + idCita + " cancelada correctamente.");
    }


}
