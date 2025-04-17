package edu.uniquindio.dentalmanagementsystembackend.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Clase de utilidad para manejar fechas y horas en la zona horaria de Bogotá, Colombia.
 */
public class DateUtil {

    // Zona horaria de Bogotá, Colombia
    public static final ZoneId ZONA_BOGOTA = ZoneId.of("America/Bogota");
    
    // Formato de fecha y hora para Colombia
    public static final DateTimeFormatter FORMATO_FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    /**
     * Crea un Instant para una fecha y hora específica en la zona horaria de Bogotá.
     * 
     * @param año Año (ejemplo: 2023)
     * @param mes Mes (1-12)
     * @param dia Día del mes (1-31)
     * @param hora Hora (0-23)
     * @param minuto Minuto (0-59)
     * @return Instant correspondiente a la fecha y hora especificada
     */
    public static Instant crearFechaHoraBogota(int año, int mes, int dia, int hora, int minuto) {
        LocalDateTime fechaHoraLocal = LocalDateTime.of(año, mes, dia, hora, minuto);
        return fechaHoraLocal.atZone(ZONA_BOGOTA).toInstant();
    }
    
    /**
     * Formatea un Instant a una cadena de texto en formato dd/MM/yyyy HH:mm
     * 
     * @param instant El Instant a formatear
     * @return Cadena formateada
     */
    public static String formatearFechaHora(Instant instant) {
        LocalDateTime fechaHoraLocal = instant.atZone(ZONA_BOGOTA).toLocalDateTime();
        return fechaHoraLocal.format(FORMATO_FECHA_HORA);
    }
    
    /**
     * Convierte una cadena de texto en formato dd/MM/yyyy HH:mm a un Instant
     * 
     * @param fechaHoraStr Cadena en formato dd/MM/yyyy HH:mm
     * @return Instant correspondiente
     */
    public static Instant parsearFechaHora(String fechaHoraStr) {
        LocalDateTime fechaHoraLocal = LocalDateTime.parse(fechaHoraStr, FORMATO_FECHA_HORA);
        return fechaHoraLocal.atZone(ZONA_BOGOTA).toInstant();
    }
} 