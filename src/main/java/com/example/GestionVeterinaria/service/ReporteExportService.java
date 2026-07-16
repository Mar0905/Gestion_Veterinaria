package com.example.GestionVeterinaria.service;

import com.example.GestionVeterinaria.entity.Comprobante;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReporteExportService {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Genera un archivo Excel con el reporte de facturación del periodo indicado
    public byte[] crearExcel(List<Comprobante> comprobantes, LocalDate desde, LocalDate hasta) {
        try (XSSFWorkbook libro = new XSSFWorkbook(); ByteArrayOutputStream salida = new ByteArrayOutputStream()) {
            var hoja = libro.createSheet("Facturación");
            XSSFFont fuenteTitulo = libro.createFont();
            fuenteTitulo.setBold(true);
            fuenteTitulo.setFontHeightInPoints((short) 14);
            CellStyle titulo = libro.createCellStyle(); titulo.setFont(fuenteTitulo);
            CellStyle cabecera = libro.createCellStyle();
            cabecera.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            cabecera.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            XSSFFont fuenteCabecera = libro.createFont(); fuenteCabecera.setColor(IndexedColors.WHITE.getIndex()); fuenteCabecera.setBold(true);
            cabecera.setFont(fuenteCabecera);
            CellStyle moneda = libro.createCellStyle(); moneda.setDataFormat(libro.createDataFormat().getFormat("S/ #,##0.00"));

            hoja.createRow(0).createCell(0).setCellValue("Reporte de facturación");
            hoja.getRow(0).getCell(0).setCellStyle(titulo);
            hoja.createRow(1).createCell(0).setCellValue("Periodo: " + desde.format(FECHA) + " al " + hasta.format(FECHA));
            String[] columnas = {"N°", "Fecha", "Cliente", "Tipo", "Pago", "Subtotal", "IGV", "Total"};
            var filaCabecera = hoja.createRow(3);
            for (int i = 0; i < columnas.length; i++) { Cell celda = filaCabecera.createCell(i); celda.setCellValue(columnas[i]); celda.setCellStyle(cabecera); }

            int fila = 4;
            BigDecimal total = BigDecimal.ZERO;
            for (Comprobante c : comprobantes) {
                var datos = hoja.createRow(fila++);
                datos.createCell(0).setCellValue(c.getId());
                datos.createCell(1).setCellValue(c.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                datos.createCell(2).setCellValue(c.getCliente().getNombre() + " " + c.getCliente().getApellido());
                datos.createCell(3).setCellValue(c.getTipo());
                datos.createCell(4).setCellValue(c.getMetodoPago());
                crearMoneda(datos, 5, c.getSubtotal(), moneda);
                crearMoneda(datos, 6, c.getIgv(), moneda);
                crearMoneda(datos, 7, c.getTotal(), moneda);
                total = total.add(c.getTotal());
            }
            var totales = hoja.createRow(fila);
            totales.createCell(6).setCellValue("TOTAL");
            crearMoneda(totales, 7, total, moneda);
            for (int i = 0; i < columnas.length; i++) hoja.autoSizeColumn(i);
            hoja.createFreezePane(0, 4);
            libro.write(salida);
            return salida.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo generar el Excel", e);
        }
    }

    // Genera un archivo PDF con el reporte de facturación del periodo indicado
    public byte[] crearPdf(List<Comprobante> comprobantes, LocalDate desde, LocalDate hasta) {
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        Document documento = new Document(PageSize.A4.rotate(), 30, 30, 30, 30);
        PdfWriter.getInstance(documento, salida);
        documento.open();
        Font titulo = new Font(Font.HELVETICA, 16, Font.BOLD);
        documento.add(new Paragraph("Reporte de facturación", titulo));
        documento.add(new Paragraph("Periodo: " + desde.format(FECHA) + " al " + hasta.format(FECHA)));
        documento.add(new Paragraph(" "));
        PdfPTable tabla = new PdfPTable(new float[]{.6f, 1.4f, 2.5f, 1f, 1.3f, 1.2f});
        tabla.setWidthPercentage(100);
        for (String encabezado : new String[]{"N°", "Fecha", "Cliente", "Tipo", "Pago", "Total"}) tabla.addCell(celda(encabezado, true));
        BigDecimal total = BigDecimal.ZERO;
        for (Comprobante c : comprobantes) {
            tabla.addCell(celda(String.valueOf(c.getId()), false));
            tabla.addCell(celda(c.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), false));
            tabla.addCell(celda(c.getCliente().getNombre() + " " + c.getCliente().getApellido(), false));
            tabla.addCell(celda(c.getTipo(), false));
            tabla.addCell(celda(c.getMetodoPago(), false));
            tabla.addCell(celda("S/ " + c.getTotal(), false));
            total = total.add(c.getTotal());
        }
        documento.add(tabla);
        Paragraph totalParrafo = new Paragraph("Total facturado: S/ " + total, new Font(Font.HELVETICA, 12, Font.BOLD));
        totalParrafo.setAlignment(Element.ALIGN_RIGHT);
        documento.add(totalParrafo);
        documento.close();
        return salida.toByteArray();
    }

    private void crearMoneda(org.apache.poi.ss.usermodel.Row fila, int indice, BigDecimal valor, CellStyle estilo) {
        Cell celda = fila.createCell(indice); celda.setCellValue(valor.doubleValue()); celda.setCellStyle(estilo);
    }

    private PdfPCell celda(String texto, boolean encabezado) {
        PdfPCell celda = new PdfPCell(new Phrase(texto));
        celda.setPadding(6);
        if (encabezado) { celda.setBackgroundColor(new java.awt.Color(30, 64, 175)); celda.getPhrase().getFont().setColor(java.awt.Color.WHITE); }
        return celda;
    }
}
