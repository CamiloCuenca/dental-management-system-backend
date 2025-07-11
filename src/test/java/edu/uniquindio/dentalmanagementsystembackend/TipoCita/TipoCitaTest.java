package edu.uniquindio.dentalmanagementsystembackend.TipoCita;

import java.util.List;

import edu.uniquindio.dentalmanagementsystembackend.dto.cita.TipoCitaDTO;
import edu.uniquindio.dentalmanagementsystembackend.entity.TipoCita;
import edu.uniquindio.dentalmanagementsystembackend.repository.TipoCitaRepository;
import edu.uniquindio.dentalmanagementsystembackend.service.impl.ServiciosTipoCitaImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TipoCitaTest {

    @Mock
    private TipoCitaRepository tipoCitaRepository;
    @InjectMocks
    private ServiciosTipoCitaImpl serviciosTipoCita;

    private TipoCita tipoCita;

    @BeforeEach
    void setUp() {
        tipoCita = new TipoCita();
        tipoCita.setId(1L);
        tipoCita.setNombre("Consulta General");
        tipoCita.setDuracionMinutos(30);
        tipoCita.setDescripcion("Consulta básica");
    }

    @Test
    void listarTiposCita() {
        when(tipoCitaRepository.findAll()).thenReturn(List.of(tipoCita));
        List<TipoCitaDTO> tiposCita = serviciosTipoCita.listarTiposCita();
        assertNotNull(tiposCita);
        assertEquals(1, tiposCita.size());
        assertEquals("Consulta General", tiposCita.get(0).nombre());
        verify(tipoCitaRepository).findAll();
    }
}
