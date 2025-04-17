package edu.uniquindio.dentalmanagementsystembackend.controller;

import edu.uniquindio.dentalmanagementsystembackend.dto.cita.CrearCitaNoAutenticadaDTO;
import edu.uniquindio.dentalmanagementsystembackend.entity.Cita;
import edu.uniquindio.dentalmanagementsystembackend.service.Interfaces.ServiciosCitas;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/citas-no-autenticadas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CitaNoAutenticadaController {

    private final ServiciosCitas serviciosCitas;

    @PostMapping
    public ResponseEntity<Cita> crearCitaNoAutenticada(@Valid @RequestBody CrearCitaNoAutenticadaDTO crearCitaNoAutenticadaDTO) {
        try {
            Cita cita = serviciosCitas.crearCitaNoAutenticada(crearCitaNoAutenticadaDTO);
            return ResponseEntity.ok(cita);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 