package edu.uniquindio.dentalmanagementsystembackend.controller;

import edu.uniquindio.dentalmanagementsystembackend.dto.historial.CrearHistorialDTO;
import edu.uniquindio.dentalmanagementsystembackend.dto.historial.HistorialDTO;
import edu.uniquindio.dentalmanagementsystembackend.entity.Account.HistorialMedico;
import edu.uniquindio.dentalmanagementsystembackend.service.Interfaces.HistorialService;
import edu.uniquindio.dentalmanagementsystembackend.service.impl.PdfGenerator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/historiales")
@RequiredArgsConstructor
public class HistorialController {


    // 1. Declara el logger
    private static final Logger logger = LoggerFactory.getLogger(HistorialController.class);


    private final HistorialService historialService;


    private final  PdfGenerator pdfGenerator;


    /**
     * Crea un nuevo historial médico utilizando los datos proporcionados en el DTO.
     * Este método procesa la información recibida, invoca el servicio correspondiente
     * para crear el historial médico y retorna la respuesta con la entidad creada.
     *
     * @param crearHistorialDTO Objeto DTO que contiene los datos necesarios
     *                          para crear un nuevo historial médico. Debe ser válido.
     * @return ResponseEntity que contiene la entidad del historial médico creado
     *         y un estado HTTP de éxito.
     *
     */
    @PostMapping("/crear")
    public ResponseEntity<HistorialMedico> crearHistorial(@Valid @RequestBody CrearHistorialDTO crearHistorialDTO) {
        HistorialMedico historialCreado = historialService.crearHistorial(crearHistorialDTO);
        return ResponseEntity.ok(historialCreado);
    }

    /**
     * Endpoint para listar los historiales clínicos de un paciente agrupados por año.
     * Este método permite obtener los historiales correspondientes a un paciente específico,
     * organizados en un mapa donde la clave es el año y el valor es una lista de historiales de ese año.
     *
     * @param idNumber Identificador único del paciente cuyo historial se desea consultar.
     * @return Un objeto ResponseEntity que contiene un mapa con los años como claves y
     *         listas de objetos HistorialDTO como valores. Devuelve el código de respuesta HTTP correspondiente.
     */
    @GetMapping("/paciente/{idPaciente}/agrupado-por-anio")
    public ResponseEntity<Map<Integer, List<HistorialDTO>>> listarHistorialesPorPacienteAgrupadosPorAnio(
            @PathVariable("idPaciente") String idNumber) {
        Map<Integer, List<HistorialDTO>> historialAgrupado = historialService.listarHistorialesPorPacienteAgrupadosPorAnio(idNumber);
        return ResponseEntity.ok(historialAgrupado);
    }

    /**
     * Método para listar los historiales médicos de un paciente específico en un año determinado.
     *
     * @param idNumber Identificador único del paciente cuyos historiales se desean consultar.
     * @param anio Año en el cual se filtran los historiales médicos.
     * @return Un ResponseEntity que contiene una lista de objetos HistorialDTO correspondiente
     *         a los historiales médicos encontrados, o una lista vacía si no existen registros
     *         para el paciente y año especificados.
     */
    @GetMapping("/paciente/{idPaciente}/anio/{anio}")
    public ResponseEntity<List<HistorialDTO>> listarHistorialesPorPacienteYAnio(
            @PathVariable("idPaciente") String idNumber,
            @PathVariable("anio") int anio) {
        List<HistorialDTO> historiales = historialService.listarHistorialesPorPacienteYAnio(idNumber, anio);
        return ResponseEntity.ok(historiales);
    }

    /**
     * Genera un archivo PDF que contiene el historial médico del paciente especificado
     * por su identificador y lo devuelve en la respuesta HTTP.
     *
     * @param id Identificador único del paciente cuyo historial médico se quiere generar.
     *           Debe ser una cadena válida y no vacía.
     * @return Un objeto ResponseEntity que contiene el byte array del PDF generado
     *         en el cuerpo de la respuesta, junto con los encabezados HTTP necesarios.
     *         En caso de error, devuelve un ResponseEntity con el estado apropiado y un mensaje en el cuerpo.
     * @throws IllegalStateException Si el PDF generado está vacío o el proceso de generación falla inesperadamente.
     */
    @PostMapping("/paciente/pdf/{id}")
    public ResponseEntity<byte[]> generarPdfHistorial(@PathVariable("id") String id) {
        try {
            // Validación básica del ID
            if (id == null || id.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("ID de paciente no válido".getBytes());
            }

            byte[] pdfBytes = pdfGenerator.historialPDF(id);

            // Verificar que el PDF no esté vacío
            if (pdfBytes == null || pdfBytes.length == 0) {
                throw new IllegalStateException("El PDF generado está vacío");
            }

            return ResponseEntity.ok()
                    .header("Content-Type", "application/pdf")
                    .header("Content-Disposition", "attachment; filename=historial_"+id+".pdf")
                    .body(pdfBytes);

        } catch (Exception e) {
            // Log del error completo
            logger.error("Error al generar PDF para paciente " + id, e);

            // Devuelve un mensaje de error claro
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error al generar PDF: " + e.getMessage()).getBytes());
        }
    }



    /**
     * Genera un archivo PDF que contiene el historial médico de un paciente para un año específico.
     * Este método se expone como una API REST y devuelve el PDF como un archivo adjunto descargable.
     *
     * @param id El identificador único del paciente. No debe ser nulo ni estar vacío.
     * @param anio El año específico del historial médico que se generará en el PDF.
     * @return Una entidad de respuesta que contiene el archivo PDF en formato de bytes.
     *         El encabezado incluye el tipo de contenido como "application/pdf" y el nombre del archivo.
     *         Si ocurre un error, devuelve un mensaje de error en el cuerpo de la respuesta con el estado HTTP correspondiente.
     * @throws IllegalStateException Si el PDF generado está vacío.
     */
    @PostMapping("/paciente/pdf/{id}/{anio}")
    public ResponseEntity<byte[]> generarPdfHistorialPorAnio(
            @PathVariable("id") String id,
            @PathVariable("anio") int anio) {
        try {
            // Validación básica del ID
            if (id == null || id.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("ID de paciente no válido".getBytes());
            }

            byte[] pdfBytes = pdfGenerator.historialPDFPorAnio(id,anio);

            // Verificar que el PDF no esté vacío
            if (pdfBytes == null || pdfBytes.length == 0) {
                throw new IllegalStateException("El PDF generado está vacío");
            }

            return ResponseEntity.ok()
                    .header("Content-Type", "application/pdf")
                    .header("Content-Disposition", "attachment; filename=historial_"+id+".pdf")
                    .body(pdfBytes);

        } catch (Exception e) {
            // Log del error completo
            logger.error("Error al generar PDF para paciente " + id, e);

            // Devuelve un mensaje de error claro
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error al generar PDF: " + e.getMessage()).getBytes());
        }
    }


}
