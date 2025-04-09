package edu.uniquindio.dentalmanagementsystembackend.Historial;

import edu.uniquindio.dentalmanagementsystembackend.dto.historial.CrearHistorialDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.historial.HistorialDTO;
import edu.uniquindio.dentalmanagementsystembackend.entity.Account.HistorialMedico;
import edu.uniquindio.dentalmanagementsystembackend.repository.HistorialMedicoRepository;
import edu.uniquindio.dentalmanagementsystembackend.repository.UserRepository;
import edu.uniquindio.dentalmanagementsystembackend.repository.CitasRepository;
import edu.uniquindio.dentalmanagementsystembackend.service.Interfaces.HistorialService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase de pruebas para el servicio de historial médico.
 * Contiene pruebas unitarias para verificar el funcionamiento correcto de todos los métodos
 * del servicio de historial médico.
 */
@SpringBootTest
@Transactional
public class HistorialTest {

    @Autowired
    private HistorialService historialService;

    @Autowired
    private HistorialMedicoRepository historialRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CitasRepository citasRepository;




}
