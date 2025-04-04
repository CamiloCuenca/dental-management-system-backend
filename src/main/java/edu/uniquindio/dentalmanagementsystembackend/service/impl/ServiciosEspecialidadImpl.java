package edu.uniquindio.dentalmanagementsystembackend.service.impl;

import edu.uniquindio.dentalmanagementsystembackend.entity.Especialidad;
import edu.uniquindio.dentalmanagementsystembackend.repository.EspecialidadRepository;
import edu.uniquindio.dentalmanagementsystembackend.service.Interfaces.ServiciosEspecialidad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ServiciosEspecialidadImpl implements ServiciosEspecialidad {

    @Autowired
    private EspecialidadRepository especialidadRepository;

    @Override
    public List<Especialidad> listarEspecialidades() {
        return especialidadRepository.findAll();
    }

    @Override
    public Especialidad obtenerEspecialidadPorId(Long id) {
        return especialidadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Especialidad no encontrada con ID: " + id));
    }

    @Override
    public Especialidad crearEspecialidad(Especialidad especialidad) {
        // Validar que el nombre no esté vacío
        if (especialidad.getNombre() == null || especialidad.getNombre().trim().isEmpty()) {
            throw new RuntimeException("El nombre de la especialidad no puede estar vacío");
        }
        
        // Validar que la descripción no esté vacía
        if (especialidad.getDescripcion() == null || especialidad.getDescripcion().trim().isEmpty()) {
            throw new RuntimeException("La descripción de la especialidad no puede estar vacía");
        }
        
        return especialidadRepository.save(especialidad);
    }

    @Override
    public Especialidad actualizarEspecialidad(Especialidad especialidad) {
        // Verificar que la especialidad existe
        if (!especialidadRepository.existsById(especialidad.getId())) {
            throw new RuntimeException("Especialidad no encontrada con ID: " + especialidad.getId());
        }
        
        // Validar que el nombre no esté vacío
        if (especialidad.getNombre() == null || especialidad.getNombre().trim().isEmpty()) {
            throw new RuntimeException("El nombre de la especialidad no puede estar vacío");
        }
        
        // Validar que la descripción no esté vacía
        if (especialidad.getDescripcion() == null || especialidad.getDescripcion().trim().isEmpty()) {
            throw new RuntimeException("La descripción de la especialidad no puede estar vacía");
        }
        
        return especialidadRepository.save(especialidad);
    }

    @Override
    public void eliminarEspecialidad(Long id) {
        // Verificar que la especialidad existe
        if (!especialidadRepository.existsById(id)) {
            throw new RuntimeException("Especialidad no encontrada con ID: " + id);
        }
        
        especialidadRepository.deleteById(id);
    }
} 