package edu.uniquindio.dentalmanagementsystembackend.service.impl;

import edu.uniquindio.dentalmanagementsystembackend.entity.TipoCita;
import edu.uniquindio.dentalmanagementsystembackend.repository.TipoCitaRepository;
import edu.uniquindio.dentalmanagementsystembackend.service.Interfaces.ServiciosTipoCita;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ServiciosTipoCitaImpl implements ServiciosTipoCita {

    @Autowired
    private TipoCitaRepository tipoCitaRepository;

    @Override
    public List<TipoCita> listarTiposCita() {
        return tipoCitaRepository.findAll();
    }

    @Override
    public TipoCita obtenerTipoCitaPorId(Long id) {
        return tipoCitaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo de cita no encontrado con ID: " + id));
    }

    @Override
    public TipoCita crearTipoCita(TipoCita tipoCita) {
        // Validar que el nombre no esté vacío
        if (tipoCita.getNombre() == null || tipoCita.getNombre().trim().isEmpty()) {
            throw new RuntimeException("El nombre del tipo de cita no puede estar vacío");
        }
        
        // Validar que la especialidad requerida no sea nula
        if (tipoCita.getEspecialidadRequerida() == null) {
            throw new RuntimeException("La especialidad requerida no puede ser nula");
        }
        
        return tipoCitaRepository.save(tipoCita);
    }

    @Override
    public TipoCita actualizarTipoCita(TipoCita tipoCita) {
        // Verificar que el tipo de cita existe
        if (!tipoCitaRepository.existsById(tipoCita.getId())) {
            throw new RuntimeException("Tipo de cita no encontrado con ID: " + tipoCita.getId());
        }
        
        // Validar que el nombre no esté vacío
        if (tipoCita.getNombre() == null || tipoCita.getNombre().trim().isEmpty()) {
            throw new RuntimeException("El nombre del tipo de cita no puede estar vacío");
        }
        
        // Validar que la especialidad requerida no sea nula
        if (tipoCita.getEspecialidadRequerida() == null) {
            throw new RuntimeException("La especialidad requerida no puede ser nula");
        }
        
        return tipoCitaRepository.save(tipoCita);
    }

    @Override
    public void eliminarTipoCita(Long id) {
        // Verificar que el tipo de cita existe
        if (!tipoCitaRepository.existsById(id)) {
            throw new RuntimeException("Tipo de cita no encontrado con ID: " + id);
        }
        
        tipoCitaRepository.deleteById(id);
    }
} 