package edu.uniquindio.dentalmanagementsystembackend.Citas;

import edu.uniquindio.dentalmanagementsystembackend.repository.CitasRepository;
import edu.uniquindio.dentalmanagementsystembackend.service.Interfaces.ServiciosCitas;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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

}
