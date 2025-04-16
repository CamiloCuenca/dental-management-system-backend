package edu.uniquindio.dentalmanagementsystembackend.service.impl;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import edu.uniquindio.dentalmanagementsystembackend.dto.historial.HistorialDTO;
import edu.uniquindio.dentalmanagementsystembackend.service.Interfaces.HistorialService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class PdfGenerator {

    @Autowired
    private  HistorialService historialService;


    public  void historialPDF(String id) {
        Document document = new Document();

        try {
            // Obtener la carpeta "Downloads" del usuario
            String userHome = System.getProperty("user.home");
            String downloadsPath = Paths.get(userHome, "Downloads", "HistorialMedico.pdf").toString();

            PdfWriter.getInstance(document, new FileOutputStream(downloadsPath));
            document.open();

            // Título principal
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, BaseColor.DARK_GRAY);
            Paragraph title = new Paragraph("HISTORIAL MÉDICO DENTAL", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20f);
            document.add(title);

            // Logo
            /**Image image = Image.getInstance(""); // Ruta del logo
            image.scaleToFit(100, 100);
            image.setAlignment(Element.ALIGN_CENTER);
            document.add(image);**/

            // Espacio después del logo
            document.add(new Paragraph(" "));

            // Llamar al servicio para listar los historiales agrupados por año
            Map<Integer, List<HistorialDTO>> historialesAgrupados = historialService.listarHistorialesPorPacienteAgrupadosPorAnio(id);


            // Recorrer por años
            for (Map.Entry<Integer, List<HistorialDTO>> entry : historialesAgrupados.entrySet()) {
                int año = entry.getKey();
                List<HistorialDTO> historiales = entry.getValue();

                // Título del año
                Font yearFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLACK);
                Paragraph yearTitle = new Paragraph("Año: " + año, yearFont);
                yearTitle.setSpacingBefore(15f);
                yearTitle.setSpacingAfter(10f);
                document.add(yearTitle);

                // Tabla para cada registro del año
                for (HistorialDTO historial : historiales) {
                    PdfPTable table = new PdfPTable(2);
                    table.setWidthPercentage(100);
                    table.setSpacingBefore(10f);
                    table.setSpacingAfter(15f);

                    // Configurar anchos de columnas
                    float[] columnWidths = {1f, 3f};
                    table.setWidths(columnWidths);

                    // Estilo para celdas de encabezado
                    Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);
                    BaseColor headerColor = new BaseColor(70, 130, 180); // Azul acero

                    // Estilo para celdas de contenido
                    Font contentFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

                    // Agregar datos
                    addTableRow(table, "Paciente:", historial.nombrePaciente(), headerFont, headerColor, contentFont);
                    addTableRow(table, "Odontólogo:", historial.nombreOdontologo(), headerFont, headerColor, contentFont);
                    addTableRow(table, "Fecha:", historial.fecha().toString(), headerFont, headerColor, contentFont);
                    addTableRow(table, "Tipo de Cita:", historial.tipoCita(), headerFont, headerColor, contentFont);
                    addTableRow(table, "Diagnóstico:", historial.diagnostico(), headerFont, headerColor, contentFont);
                    addTableRow(table, "Tratamiento:", historial.tratamiento(), headerFont, headerColor, contentFont);
                    addTableRow(table, "Observaciones:", historial.observaciones(), headerFont, headerColor, contentFont);

                    document.add(table);
                }
            }

            document.close();
            System.out.println("PDF creado correctamente en: " + downloadsPath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private  void addTableRow(PdfPTable table, String header, String content,
                                    Font headerFont, BaseColor headerColor, Font contentFont) {
        // Celda de encabezado
        PdfPCell headerCell = new PdfPCell(new Phrase(header, headerFont));
        headerCell.setBackgroundColor(headerColor);
        headerCell.setPadding(5);
        table.addCell(headerCell);

        // Celda de contenido
        PdfPCell contentCell = new PdfPCell(new Phrase(content != null ? content : "N/A", contentFont));
        contentCell.setPadding(5);
        table.addCell(contentCell);
    }


}