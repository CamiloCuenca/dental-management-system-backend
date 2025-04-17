package edu.uniquindio.dentalmanagementsystembackend.service.impl;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import edu.uniquindio.dentalmanagementsystembackend.dto.historial.HistorialDTO;
import edu.uniquindio.dentalmanagementsystembackend.service.Interfaces.HistorialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class PdfGenerator {

    @Autowired
    private HistorialService historialService;

    // Colores personalizados
    private static final BaseColor COLOR_PRINCIPAL = new BaseColor(63, 81, 181); // Azul profesional
    private static final BaseColor COLOR_SECUNDARIO = new BaseColor(233, 30, 99); // Rosa dental
    private static final BaseColor COLOR_FONDO = new BaseColor(245, 245, 245); // Gris claro
    private static final BaseColor COLOR_TEXTO = new BaseColor(33, 33, 33); // Gris oscuro
    private static final BaseColor COLOR_ID_PACIENTE = new BaseColor(120, 120, 120); // Gris para ID

    public void historialPDF(String id) {
        // Configurar documento con márgenes
        Document document = new Document(PageSize.A4, 40, 40, 80, 40);

        try {
            String userHome = System.getProperty("user.home");
            String downloadsPath = Paths.get(userHome, "Downloads", "HistorialDental_" + id + ".pdf").toString();

            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(downloadsPath));

            // Configurar evento para header/footer
            writer.setPageEvent(new PdfPageEventHelper() {
                @Override
                public void onEndPage(PdfWriter writer, Document document) {
                    try {
                        // Header
                        PdfPTable header = new PdfPTable(1);
                        header.setWidthPercentage(100);

                        Paragraph clinicName = new Paragraph("CLÍNICA DENTAL UNIQUINDIO",
                                new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.GRAY));
                        clinicName.setAlignment(Element.ALIGN_RIGHT);

                        PdfPCell cell = new PdfPCell(clinicName);
                        cell.setBorder(Rectangle.NO_BORDER);
                        cell.setPaddingBottom(10);
                        header.addCell(cell);

                        header.writeSelectedRows(0, -1, document.left(), document.top() + 20, writer.getDirectContent());

                        // Footer
                        PdfPTable footer = new PdfPTable(1);
                        footer.setWidthPercentage(100);

                        Paragraph footerText = new Paragraph("Paciente ID: " + id + " • Página " + writer.getPageNumber(),
                                new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL, BaseColor.LIGHT_GRAY));
                        footerText.setAlignment(Element.ALIGN_CENTER);

                        PdfPCell footerCell = new PdfPCell(footerText);
                        footerCell.setBorder(Rectangle.NO_BORDER);
                        footerCell.setPaddingTop(10);
                        footer.addCell(footerCell);

                        footer.writeSelectedRows(0, -1, document.left(), document.bottom() - 20, writer.getDirectContent());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            document.open();

            // Fuentes personalizadas
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD, COLOR_PRINCIPAL);
            Font patientFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLDITALIC, COLOR_SECUNDARIO);
            Font patientIdFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, COLOR_ID_PACIENTE);
            Font yearFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.WHITE);
            Font sectionFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, COLOR_PRINCIPAL);

            // Título principal con decoración
            Paragraph title = new Paragraph("HISTORIAL DENTAL", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(15f);
            document.add(title);

            // Línea decorativa
            addDecoratedLine(document, COLOR_SECUNDARIO);

            // Obtener datos del historial
            Map<Integer, List<HistorialDTO>> historialesAgrupados = historialService.listarHistorialesPorPacienteAgrupadosPorAnio(id);

            // Subtítulo con nombre del paciente y ID
            if (!historialesAgrupados.isEmpty()) {
                String nombrePaciente = historialesAgrupados.values().iterator().next().get(0).nombrePaciente();

                // Contenedor para nombre e ID
                Paragraph patientContainer = new Paragraph();
                patientContainer.setAlignment(Element.ALIGN_CENTER);
                patientContainer.setSpacingAfter(20f);

                // Nombre del paciente
                Paragraph patientName = new Paragraph(nombrePaciente, patientFont);
                patientName.setAlignment(Element.ALIGN_CENTER);
                patientContainer.add(patientName);

                // ID del paciente (en gris y más pequeño)
                Paragraph patientId = new Paragraph("ID: " + id, patientIdFont);
                patientId.setAlignment(Element.ALIGN_CENTER);
                patientId.setSpacingAfter(5f);
                patientContainer.add(patientId);

                document.add(patientContainer);
            }

            // Resto del código permanece igual...
            // Sección por años
            for (Map.Entry<Integer, List<HistorialDTO>> entry : historialesAgrupados.entrySet()) {
                int año = entry.getKey();
                List<HistorialDTO> historiales = entry.getValue();

                // Encabezado de año con estilo
                PdfPTable yearHeader = new PdfPTable(1);
                yearHeader.setWidthPercentage(30);
                yearHeader.setHorizontalAlignment(Element.ALIGN_LEFT);

                PdfPCell yearCell = new PdfPCell(new Phrase("AÑO " + año, yearFont));
                yearCell.setBackgroundColor(COLOR_PRINCIPAL);
                yearCell.setBorder(Rectangle.NO_BORDER);
                yearCell.setPadding(8);
                yearCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                yearHeader.addCell(yearCell);

                document.add(yearHeader);
                document.add(Chunk.NEWLINE);

                // Registros del año
                for (HistorialDTO historial : historiales) {
                    // Tarjeta de registro
                    PdfPTable card = new PdfPTable(1);
                    card.setWidthPercentage(100);
                    card.setSpacingBefore(10f);
                    card.setSpacingAfter(15f);
                    card.getDefaultCell().setBorder(Rectangle.NO_BORDER);

                    // Cabecera de tarjeta
                    PdfPCell headerCell = new PdfPCell();
                    headerCell.setBackgroundColor(COLOR_FONDO);
                    headerCell.setBorder(Rectangle.NO_BORDER);
                    headerCell.setPadding(8);

                    Paragraph cardHeader = new Paragraph();
                    cardHeader.add(new Chunk("Consulta: ",
                            new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, COLOR_TEXTO)));
                    cardHeader.add(new Chunk(historial.fecha().toString(),
                            new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, COLOR_TEXTO)));
                    cardHeader.add(new Chunk(" • ",
                            new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, COLOR_TEXTO)));
                    cardHeader.add(new Chunk(historial.tipoCita(),
                            new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, COLOR_SECUNDARIO)));

                    headerCell.addElement(cardHeader);
                    card.addCell(headerCell);

                    // Cuerpo de tarjeta
                    PdfPTable infoTable = new PdfPTable(2);
                    infoTable.setWidthPercentage(100);
                    infoTable.setWidths(new float[]{1, 3});
                    infoTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
                    infoTable.getDefaultCell().setPadding(5);

                    addStyledInfoRow(infoTable, "ODONTÓLOGO", historial.nombreOdontologo());
                    addStyledInfoRow(infoTable, "DIAGNÓSTICO", historial.diagnostico());
                    addStyledInfoRow(infoTable, "TRATAMIENTO", historial.tratamiento());

                    if (historial.observaciones() != null && !historial.observaciones().isEmpty()) {
                        addStyledInfoRow(infoTable, "OBSERVACIONES", historial.observaciones());
                    }

                    card.addCell(infoTable);

                    // Línea decorativa inferior
                    PdfPCell lineCell = new PdfPCell();
                    lineCell.setBorder(Rectangle.NO_BORDER);
                    lineCell.setPadding(3);

                    Paragraph line = new Paragraph();
                    line.add(new Chunk(" ", new Font(Font.FontFamily.HELVETICA, 5)));
                    line.add(new Chunk("• • • • • • • • • • • • • • • • • • • • • • • • • • • • • • • •",
                            new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL, COLOR_SECUNDARIO)));
                    lineCell.addElement(line);

                    card.addCell(lineCell);
                    document.add(card);
                }
            }

            document.close();
            System.out.println("PDF creado correctamente en: " + downloadsPath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addStyledInfoRow(PdfPTable table, String header, String content) {
        // Celda de encabezado
        PdfPCell headerCell = new PdfPCell(new Phrase(header,
                new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, COLOR_TEXTO)));
        headerCell.setBorder(Rectangle.NO_BORDER);
        headerCell.setPadding(5);
        table.addCell(headerCell);

        // Celda de contenido
        PdfPCell contentCell = new PdfPCell(new Phrase(content != null ? content : "N/A",
                new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, COLOR_TEXTO)));
        contentCell.setBorder(Rectangle.NO_BORDER);
        contentCell.setPadding(5);
        table.addCell(contentCell);
    }

    private void addDecoratedLine(Document document, BaseColor color) throws DocumentException {
        Paragraph line = new Paragraph();
        line.add(new Chunk("____________________________________________________________",
                new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, color)));
        line.setAlignment(Element.ALIGN_CENTER);
        line.setSpacingAfter(15f);
        document.add(line);
    }


}