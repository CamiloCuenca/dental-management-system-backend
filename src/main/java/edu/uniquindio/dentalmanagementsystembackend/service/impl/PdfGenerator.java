package edu.uniquindio.dentalmanagementsystembackend.service.impl;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import edu.uniquindio.dentalmanagementsystembackend.dto.historial.HistorialDTO;
import edu.uniquindio.dentalmanagementsystembackend.service.Interfaces.HistorialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

/**
 * Clase encargada de generar documentos PDF relacionados con los historiales médicos de los pacientes.
 * Proporciona métodos para crear PDFs completos y segmentados por año, incluyendo detalles como encabezados,
 * pies de página, información del paciente, y registros de historial médico.
 */
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

    /**
     * Genera un PDF con el historial médico del paciente
     * @param id Identificador del paciente
     * @return byte[] con el contenido del PDF generado
     * @throws DocumentException Si ocurre un error al generar el PDF
     */
    public byte[] historialPDF(String id) throws DocumentException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 40, 40, 80, 40); // Márgenes: izquierda, derecha, arriba, abajo

        try {
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);

            // Configurar eventos para header y footer
            writer.setPageEvent(new PdfPageEventHelper() {
                @Override
                public void onEndPage(PdfWriter writer, Document document) {
                    try {
                        agregarHeader(writer, document);
                        agregarFooter(writer, document, id);
                    } catch (DocumentException e) {
                        throw new RuntimeException("Error al agregar header/footer", e);
                    }
                }
            });

            document.open();

            // Obtener datos del historial agrupados por año
            Map<Integer, List<HistorialDTO>> historialesAgrupados = historialService.listarHistorialesPorPacienteAgrupadosPorAnio(id);

            if (historialesAgrupados.isEmpty()) {
                agregarMensajeSinHistorial(document);
            } else {
                agregarContenidoPrincipal(document, id, historialesAgrupados);
            }

            document.close();
            return outputStream.toByteArray();

        } catch (Exception e) {
            if (document.isOpen()) {
                document.close();
            }
            throw new DocumentException("Error al generar el PDF: " + e.getMessage());
        }
    }

    // Métodos auxiliares privados





    /**
     * Adds a header to the provided PDF document. The header contains
     * the clinic name formatted and aligned at the top-right of the page.
     *
     * @param writer the PdfWriter instance used to write content to the PDF document
     * @param document the Document instance representing the PDF where the header will be added
     * @throws DocumentException if an error occurs while adding the header to the document
     */
    private void agregarHeader(PdfWriter writer, Document document) throws DocumentException {
        PdfPTable header = new PdfPTable(1);
        header.setTotalWidth(document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin());
        header.setLockedWidth(true);
        header.setWidthPercentage(100);

        Paragraph clinicName = new Paragraph("CLÍNICA DENTAL ODONTOLOGIC",
                new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.GRAY));
        clinicName.setAlignment(Element.ALIGN_RIGHT);

        PdfPCell cell = new PdfPCell(clinicName);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPaddingBottom(10);
        header.addCell(cell);

        header.writeSelectedRows(0, -1, document.left(), document.top() + 20, writer.getDirectContent());
    }


    /**
     * Agrega un pie de página al documento PDF, incluyendo el ID del paciente y el número de página actual.
     *
     * @param writer El objeto PdfWriter responsable de escribir el contenido del documento PDF.
     * @param document El objeto Document que representa el documento PDF en el que se añadirá el pie de página.
     * @param patientId El identificador único del paciente que se mostrará en el pie de página.
     * @throws DocumentException Si ocurre un error al modificar o escribir en el documento.
     */
    private void agregarFooter(PdfWriter writer, Document document, String patientId) throws DocumentException {
        PdfPTable footer = new PdfPTable(1);
        footer.setTotalWidth(document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin());
        footer.setLockedWidth(true);
        footer.setWidthPercentage(100);

        Paragraph footerText = new Paragraph("Paciente ID: " + patientId + " • Página " + writer.getPageNumber(),
                new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL, BaseColor.LIGHT_GRAY));
        footerText.setAlignment(Element.ALIGN_CENTER);

        PdfPCell footerCell = new PdfPCell(footerText);
        footerCell.setBorder(Rectangle.NO_BORDER);
        footerCell.setPaddingTop(10);
        footer.addCell(footerCell);

        footer.writeSelectedRows(0, -1, document.left(), document.bottom() - 20, writer.getDirectContent());
    }


    /**
     * Agrega un mensaje al documento indicando que no se encontraron registros médicos
     * para el paciente. Este mensaje se añade sin afectar el historial del documento.
     *
     * @param document el documento al que se agregará el mensaje.
     * @throws DocumentException si ocurre un error al intentar agregar el párrafo
     *         al documento.
     */
    private void agregarMensajeSinHistorial(Document document) throws DocumentException {
        Font font = new Font(Font.FontFamily.HELVETICA, 16, Font.ITALIC, BaseColor.RED);
        Paragraph noData = new Paragraph("No se encontraron registros médicos para este paciente", font);
        noData.setAlignment(Element.ALIGN_CENTER);
        noData.setSpacingBefore(200f);
        document.add(noData);
    }

    /**
     * Agrega el contenido principal al documento PDF proporcionado. Este contenido incluye
     * el título principal, una línea decorativa, la información del paciente, y una sección
     * con el historial dental agrupado por años.
     *
     * @param document el documento PDF al que se añadirá el contenido
     * @param id el identificador único del paciente
     * @param historialesAgrupados un mapa que contiene el historial dental del paciente
     *        agrupado por años, donde la clave es el año y el valor es una lista de objetos
     *        HistorialDTO que representan los registros asociados al año
     * @throws DocumentException si ocurre un error al añadir elementos al documento
     */
    private void agregarContenidoPrincipal(Document document, String id, Map<Integer, List<HistorialDTO>> historialesAgrupados)
            throws DocumentException {
        // Título principal
        Paragraph title = new Paragraph("HISTORIAL DENTAL",
                new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD, COLOR_PRINCIPAL));
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(15f);
        document.add(title);

        // Línea decorativa
        agregarLineaDecorativa(document);

        // Información del paciente
        agregarInfoPaciente(document, id, historialesAgrupados);

        // Historial por años
        for (Map.Entry<Integer, List<HistorialDTO>> entry : historialesAgrupados.entrySet()) {
            agregarSeccionAnio(document, entry.getKey(), entry.getValue());
        }
    }

    /**
     * Este método agrega una línea decorativa en un documento PDF como un elemento de formato.
     * La línea se presenta centrada, con un espaciado en la parte inferior, y utiliza una fuente específica.
     *
     * @param document el objeto de tipo Document al que se añadirá la línea decorativa
     * @throws DocumentException si ocurre un error al intentar agregar la línea al documento
     */
    private void agregarLineaDecorativa(Document document) throws DocumentException {
        Paragraph line = new Paragraph();
        line.add(new Chunk("____________________________________________________________",
                new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, COLOR_SECUNDARIO)));
        line.setAlignment(Element.ALIGN_CENTER);
        line.setSpacingAfter(15f);
        document.add(line);
    }

    /**
     * Agrega información de un paciente al documento PDF. Se incluye el nombre del paciente y su
     * identificador de manera centrada y estilizada.
     *
     * @param document El documento PDF donde se añadirá la información del paciente.
     * @param id El identificador único del paciente.
     * @param historialesAgrupados Un mapa que contiene listas de objetos HistorialDTO agrupados por un
     *                             entero. Se utiliza para obtener el nombre del paciente.
     * @throws DocumentException Si ocurre un error al agregar contenido al documento.
     */
    private void agregarInfoPaciente(Document document, String id, Map<Integer, List<HistorialDTO>> historialesAgrupados)
            throws DocumentException {
        String nombrePaciente = historialesAgrupados.values().iterator().next().get(0).nombrePaciente();

        Paragraph patientName = new Paragraph(nombrePaciente.toUpperCase(),
                new Font(Font.FontFamily.HELVETICA, 18, Font.BOLDITALIC, COLOR_SECUNDARIO));
        patientName.setAlignment(Element.ALIGN_CENTER);
        document.add(patientName);

        Paragraph patientId = new Paragraph("ID: " + id,
                new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, COLOR_ID_PACIENTE));
        patientId.setAlignment(Element.ALIGN_CENTER);
        patientId.setSpacingAfter(20f);
        document.add(patientId);
    }

    /**
     * Agrega una sección al documento correspondiente a un año específico, incluyendo
     * un encabezado con el año y los registros del historial asociados a dicho año.
     *
     * @param document El documento PDF al que se añadirá la sección.
     * @param año El año que se incluirá en la sección como encabezado.
     * @param historiales Una lista de objetos HistorialDTO que representan los registros
     *                    asociados al año especificado.
     * @throws DocumentException Si ocurre un error durante la manipulación del documento PDF.
     */
    private void agregarSeccionAnio(Document document, int año, List<HistorialDTO> historiales) throws DocumentException {
        // Encabezado de año
        PdfPTable yearHeader = new PdfPTable(1);
        yearHeader.setTotalWidth(200); // Ancho fijo para el encabezado del año
        yearHeader.setLockedWidth(true);
        yearHeader.setHorizontalAlignment(Element.ALIGN_LEFT);

        PdfPCell yearCell = new PdfPCell(new Phrase("AÑO " + año,
                new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.WHITE)));
        yearCell.setBackgroundColor(COLOR_PRINCIPAL);
        yearCell.setBorder(Rectangle.NO_BORDER);
        yearCell.setPadding(8);
        yearCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        yearHeader.addCell(yearCell);

        document.add(yearHeader);
        document.add(Chunk.NEWLINE);

        // Registros del año
        for (HistorialDTO historial : historiales) {
            agregarRegistroHistorial(document, historial);
        }
    }

    /**
     * Agrega un registro al historial en un documento PDF mediante una tarjeta que incluye información
     * relevante como fecha, tipo de cita, odontólogo, diagnóstico, tratamiento y observaciones.
     *
     * @param document El documento PDF donde se agregará el registro del historial.
     * @param historial Un objeto de tipo {@link HistorialDTO} que contiene la información detallada
     *                  del registro a agregar, incluyendo datos como fecha, tipo de cita, odontólogo,
     *                  diagnóstico, tratamiento y observaciones.
     * @throws DocumentException Si ocurre un error al modificar el documento PDF, como problemas de
     *                           formato o escritura del contenido.
     */
    private void agregarRegistroHistorial(Document document, HistorialDTO historial) throws DocumentException {
        // Tarjeta de registro
        PdfPTable card = new PdfPTable(1);
        card.setTotalWidth(document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin());
        card.setLockedWidth(true);
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
        infoTable.setTotalWidth(document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin());
        infoTable.setLockedWidth(true);
        infoTable.setWidthPercentage(100);
        infoTable.setWidths(new float[]{1, 3});
        infoTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
        infoTable.getDefaultCell().setPadding(5);

        agregarFilaInfo(infoTable, "ODONTÓLOGO", historial.nombreOdontologo());
        agregarFilaInfo(infoTable, "DIAGNÓSTICO", historial.diagnostico());
        agregarFilaInfo(infoTable, "TRATAMIENTO", historial.tratamiento());

        if (historial.observaciones() != null && !historial.observaciones().isEmpty()) {
            agregarFilaInfo(infoTable, "OBSERVACIONES", historial.observaciones());
        }

        card.addCell(infoTable);
        agregarLineaDecorativaCard(card);
        document.add(card);
    }

    /**
     * Agrega una fila de información a una tabla PDF específica, compuesta de un encabezado y su contenido correspondiente.
     *
     * @param table La tabla PDF a la cual se añadirá la nueva fila.
     * @param header El texto que será utilizado como encabezado de la celda.
     * @param content El texto que será utilizado como contenido de la celda. Si el valor es null, se mostrará "N/A".
     */
    private void agregarFilaInfo(PdfPTable table, String header, String content) {
        PdfPCell headerCell = new PdfPCell(new Phrase(header,
                new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, COLOR_TEXTO)));
        headerCell.setBorder(Rectangle.NO_BORDER);
        headerCell.setPadding(5);
        table.addCell(headerCell);

        PdfPCell contentCell = new PdfPCell(new Phrase(content != null ? content : "N/A",
                new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, COLOR_TEXTO)));
        contentCell.setBorder(Rectangle.NO_BORDER);
        contentCell.setPadding(5);
        table.addCell(contentCell);
    }

    /**
     * Agrega una línea decorativa a un elemento de tipo PdfPTable, representada
     * por una secuencia de puntos decorativos en un formato de párrafo.
     *
     * @param card El objeto PdfPTable al cual se le agregará la línea decorativa.
     * @throws DocumentException Si ocurre un error al manipular el documento PDF.
     */
    private void agregarLineaDecorativaCard(PdfPTable card) throws DocumentException {
        PdfPCell lineCell = new PdfPCell();
        lineCell.setBorder(Rectangle.NO_BORDER);
        lineCell.setPadding(3);

        Paragraph line = new Paragraph();
        line.add(new Chunk("• • • • • • • • • • • • • • • • • • • • • • • • • • • • • • • •",
                new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL, COLOR_SECUNDARIO)));
        lineCell.addElement(line);

        card.addCell(lineCell);
    }



    /**
     * Genera un archivo PDF que contiene el historial de un paciente para un año específico.
     * El documento incluye encabezado, pie de página, y los datos respectivos
     * según exista información o no en el año indicado.
     *
     * @param id Identificador único del paciente cuya información del historial se quiere generar.
     * @param anio Año específico para el cual se generará el historial en formato PDF.
     * @return Un arreglo de bytes que representa el archivo PDF generado con el contenido del historial.
     * @throws DocumentException Si ocurre algún error durante la generación o creación del archivo PDF.
     */
    public byte[] historialPDFPorAnio(String id, int anio) throws DocumentException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 40, 40, 80, 40); // Márgenes: izquierda, derecha, arriba, abajo

        try {
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);

            // Configurar eventos para header y footer (reutilizado del método original)
            writer.setPageEvent(new PdfPageEventHelper() {
                @Override
                public void onEndPage(PdfWriter writer, Document document) {
                    try {
                        agregarHeader(writer, document);
                        agregarFooter(writer, document, id);
                    } catch (DocumentException e) {
                        throw new RuntimeException("Error al agregar header/footer", e);
                    }
                }
            });

            document.open();

            // Obtener datos del historial para el año específico
            List<HistorialDTO> historiales = historialService.listarHistorialesPorPacienteYAnio(id, anio);

            if (historiales.isEmpty()) {
                agregarMensajeSinHistorial(document);
            } else {
                // Adaptación del contenido principal para un solo año
                agregarContenidoPrincipalPorAnio(document, id, anio, historiales);
            }

            document.close();
            return outputStream.toByteArray();

        } catch (Exception e) {
            if (document.isOpen()) {
                document.close();
            }
            throw new DocumentException("Error al generar el PDF: " + e.getMessage());
        }
    }

    /**
     * Método auxiliar para agregar contenido principal relacionado con un año específico
     * a un documento PDF, incluyendo un título, información del paciente,
     * y los registros correspondientes a dicho año.
     *
     * @param document el documento PDF donde se agregará el contenido
     * @param id el identificador del paciente
     * @param anio el año correspondiente al contenido que se va a agregar
     * @param historiales la lista de objetos {@code HistorialDTO} que contiene el historial del paciente
     * @throws DocumentException si ocurre un error al modificar el documento PDF
     */
    // Método auxiliar para agregar contenido principal para un año específico
    private void agregarContenidoPrincipalPorAnio(Document document, String id, int anio, List<HistorialDTO> historiales)
            throws DocumentException {
        // Título principal modificado para indicar que es de un año específico
        Paragraph title = new Paragraph("HISTORIAL DENTAL " + anio,
                new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD, COLOR_PRINCIPAL));
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(15f);
        document.add(title);

        // Línea decorativa (reutilizada)
        agregarLineaDecorativa(document);

        // Información del paciente (adaptada)
        agregarInfoPacientePorAnio(document, id, historiales);

        // Agregar los registros del año (reutilizando el método existente)
        agregarSeccionAnio(document, anio, historiales);
    }

    /**
     * Agrega información de un paciente para un año específico al documento PDF proporcionado.
     * Incluye el nombre del paciente, el ID y da formato adecuado a estos elementos en el documento.
     *
     * @param document el documento PDF al que se agregará la información del paciente
     * @param id el identificador único del paciente
     * @param historiales una lista de objetos HistorialDTO que contiene información del historial del paciente.
     *        Se utiliza el primer elemento de la lista para obtener el nombre del paciente.
     *        Si la lista está vacía, se asigna "Paciente" como nombre por defecto.
     * @throws DocumentException si ocurre un error al procesar o modificar el documento PDF
     */
    // Método auxiliar para información del paciente (versión para un año)
    private void agregarInfoPacientePorAnio(Document document, String id, List<HistorialDTO> historiales)
            throws DocumentException {
        String nombrePaciente = !historiales.isEmpty() ? historiales.get(0).nombrePaciente() : "Paciente";

        Paragraph patientName = new Paragraph(nombrePaciente.toUpperCase(),
                new Font(Font.FontFamily.HELVETICA, 18, Font.BOLDITALIC, COLOR_SECUNDARIO));
        patientName.setAlignment(Element.ALIGN_CENTER);
        document.add(patientName);

        Paragraph patientId = new Paragraph("ID: " + id,
                new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, COLOR_ID_PACIENTE));
        patientId.setAlignment(Element.ALIGN_CENTER);
        patientId.setSpacingAfter(20f);
        document.add(patientId);
    }

}