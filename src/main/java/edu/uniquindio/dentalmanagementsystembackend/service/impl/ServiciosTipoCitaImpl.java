package edu.uniquindio.dentalmanagementsystembackend.service.impl;

import edu.uniquindio.dentalmanagementsystembackend.dto.cita.TipoCitaDTO;
import edu.uniquindio.dentalmanagementsystembackend.entity.TipoCita;
import edu.uniquindio.dentalmanagementsystembackend.repository.TipoCitaRepository;
import edu.uniquindio.dentalmanagementsystembackend.service.Interfaces.ServiciosTipoCita;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Esta clase implementa la interfaz ServiciosTipoCita y proporciona la lógica necesaria
 * para gestionar las operaciones relacionadas con los tipos de cita en la aplicación.
 * Utiliza la capa de repositorio para interactuar con la base de datos.
 */
@Service
@Transactional
@Slf4j
public class ServiciosTipoCitaImpl implements ServiciosTipoCita {

    @Autowired
    private TipoCitaRepository tipoCitaRepository;


    /**
     * Obtiene la lista de todos los tipos de cita disponibles en el sistema.
     * Cada tipo de cita incluye información como su identificador, nombre, duración en minutos
     * y una descripción. La información recopilada se convierte en objetos DTO para su uso.
     *
     * @return una lista de objetos TipoCitaDTO que representan los diferentes tipos de cita.
     *         Si no se encuentran tipos de cita, se devuelve una lista vacía.
     * @throws RuntimeException si ocurre un error durante la obtención de los datos.
     */
    @Override
    public List<TipoCitaDTO> listarTiposCita() {
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
            
            return tiposCita.stream()
                    .map(tipo -> new TipoCitaDTO(
                            tipo.getId(),
                            tipo.getNombre(),
                            tipo.getDuracionMinutos(),
                            tipo.getDescripcion()
                    ))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.out.println("Error al obtener tipos de cita: " + e.getMessage());
            throw new RuntimeException("Error al obtener los tipos de cita. Por favor, intente nuevamente.");
        }
    }
} 