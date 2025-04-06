package edu.uniquindio.dentalmanagementsystembackend.service.impl;

import edu.uniquindio.dentalmanagementsystembackend.entity.TipoCita;
import edu.uniquindio.dentalmanagementsystembackend.repository.TipoCitaRepository;
import edu.uniquindio.dentalmanagementsystembackend.service.Interfaces.ServiciosTipoCita;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Service
@Transactional
@Slf4j
public class ServiciosTipoCitaImpl implements ServiciosTipoCita {

    @Autowired
    private TipoCitaRepository tipoCitaRepository;
    
    @Override
    public List<TipoCita> listarTiposCita() {
        System.out.println("\n=== Obteniendo lista de tipos de cita ===");
        try {
            List<TipoCita> tiposCita = tipoCitaRepository.findAll();
            
            if (tiposCita.isEmpty()) {
                System.out.println("No se encontraron tipos de cita");
            } else {
                System.out.println("Se encontraron " + tiposCita.size() + " tipos de cita:");
                tiposCita.forEach(tipo -> {
                    System.out.println("- ID: " + tipo.getId() + 
                                     ", Nombre: " + tipo.getNombre() + 
                                     ", Duración: " + tipo.getDuracionMinutos() + " minutos");
                });
            }
            
            return tiposCita;
        } catch (Exception e) {
            System.out.println("Error al obtener tipos de cita: " + e.getMessage());
            throw new RuntimeException("Error al obtener los tipos de cita. Por favor, intente nuevamente.");
        }
    }
} 