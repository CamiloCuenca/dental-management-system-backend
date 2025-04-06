package edu.uniquindio.dentalmanagementsystembackend.TipoCita;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.transaction.Transactional;
import edu.uniquindio.dentalmanagementsystembackend.entity.TipoCita;
import edu.uniquindio.dentalmanagementsystembackend.service.Interfaces.ServiciosTipoCita;

@SpringBootTest
@Transactional
public class TipoCitaTest {

    @Autowired
    private ServiciosTipoCita serviciosTipoCita;

    @Test
    void listarTiposCita() {
        List<TipoCita> tiposCita = serviciosTipoCita.listarTiposCita();
    }
}
