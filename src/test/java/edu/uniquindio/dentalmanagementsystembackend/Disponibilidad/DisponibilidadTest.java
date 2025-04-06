package edu.uniquindio.dentalmanagementsystembackend.Disponibilidad;

import edu.uniquindio.dentalmanagementsystembackend.dto.cita.FechaDisponibleDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.cita.HorarioDisponibleDTO;
import edu.uniquindio.dentalmanagementsystembackend.service.Interfaces.ServiciosDisponibilidadDoctor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@SpringBootTest
@Transactional
public class DisponibilidadTest {

    @Autowired
    private ServiciosDisponibilidadDoctor serviciosDisponibilidadDoctor;

    @Test
    void obtenerFechasDisponibles() {
        try {
            System.out.println("Iniciando prueba de obtención de fechas disponibles");
            System.out.println("Doctor ID: 111111111");
            System.out.println("Fecha inicio: " + LocalDate.now());
            System.out.println("Fecha fin: " + LocalDate.now().plusDays(30));
            
            List<FechaDisponibleDTO> fechasDisponibles = serviciosDisponibilidadDoctor.obtenerFechasDisponibles("111111111", LocalDate.now(), LocalDate.now().plusDays(30));
            
            System.out.println("Fechas disponibles encontradas: " + fechasDisponibles.size());
            fechasDisponibles.forEach(fecha -> System.out.println("- " + fecha));
        } catch (Exception e) {
            System.out.println("Error en la prueba: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    @Test
    void obtenerHorariosDisponibles() {
        try {
            System.out.println("Iniciando prueba de obtención de horarios disponibles");
            System.out.println("Doctor ID: 111111111");
            System.out.println("Fecha: " + LocalDate.now());
            
            List<FechaDisponibleDTO> fechasDisponibles = serviciosDisponibilidadDoctor.obtenerFechasDisponibles("111111111", LocalDate.now(), LocalDate.now().plusDays(7));
            
            if (!fechasDisponibles.isEmpty()) {
                LocalDate fechaPrueba = fechasDisponibles.get(0).fecha();
                System.out.println("Usando fecha de prueba: " + fechaPrueba);
                
                List<HorarioDisponibleDTO> horariosDisponibles = serviciosDisponibilidadDoctor.obtenerHorariosDisponibles("111111111", fechaPrueba);
                
                System.out.println("Horarios disponibles encontrados: " + horariosDisponibles.size());
                horariosDisponibles.forEach(hora -> System.out.println("- " + hora.hora()));
            } else {
                System.out.println("No hay fechas disponibles para probar los horarios");
            }
        } catch (Exception e) {
            System.out.println("Error en la prueba: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}
