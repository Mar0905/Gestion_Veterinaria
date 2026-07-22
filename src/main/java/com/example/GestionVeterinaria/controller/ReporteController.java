package com.example.GestionVeterinaria.controller;

import com.example.GestionVeterinaria.service.ReporteService;
import com.example.GestionVeterinaria.service.ReporteExportService;
import com.example.GestionVeterinaria.entity.Comprobante;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.time.LocalDate;

// Controlador del módulo de reportes: muestra el dashboard de KPIs/gráficos y exporta facturación a Excel/PDF
@Controller
@RequestMapping("/reportes")
public class ReporteController {

    private final ReporteService reporteService;
    private final ReporteExportService reporteExportService;

    public ReporteController(ReporteService reporteService, ReporteExportService reporteExportService) {
        this.reporteService = reporteService;
        this.reporteExportService = reporteExportService;
    }

    // Muestra el dashboard de reportes con KPIs y gráficos del periodo indicado
    @GetMapping
    public String dashboard(@RequestParam(required = false) LocalDate desde,
                            @RequestParam(required = false) LocalDate hasta,
                            Model model) {
        LocalDate hoy = LocalDate.now();
        if (desde == null) desde = hoy.withDayOfMonth(1);
        if (hasta == null) hasta = hoy;
        if (desde.isAfter(hasta)) {
            LocalDate temporal = desde; desde = hasta; hasta = temporal;
        }
        List<Comprobante> comprobantes = reporteService.getComprobantes(desde, hasta);
        // KPIs
        model.addAttribute("totalClientes",     reporteService.getTotalClientes());
        model.addAttribute("totalMascotas",     reporteService.getTotalMascotas());
        model.addAttribute("totalVeterinarios", reporteService.getTotalVeterinarios());
        model.addAttribute("citasProgramadas",  reporteService.getCitasProgramadas());
        model.addAttribute("comprobantesDelMes",comprobantes.size());
        model.addAttribute("ingresosDelMes",    reporteService.getIngresos(comprobantes));
        model.addAttribute("stockBajo",         reporteService.getProductosStockBajo());

        // Gráfico: citas por estado
        Map<String, Long> citasMap = reporteService.getCitasPorEstado(desde, hasta);
        model.addAttribute("citasLabels", new ArrayList<>(citasMap.keySet()));
        model.addAttribute("citasCounts", new ArrayList<>(citasMap.values()));

        // Gráfico: ingresos últimos 6 meses
        Map<String, Double> ingresosMap = reporteService.getIngresosPorPeriodo(comprobantes, desde, hasta);
        model.addAttribute("ingresosLabels", new ArrayList<>(ingresosMap.keySet()));
        model.addAttribute("ingresosData", new ArrayList<>(ingresosMap.values()));

        // Gráfico: mascotas por especie
        Map<String, Long> especiesMap = reporteService.getMascotasPorEspecie();
        model.addAttribute("especiesLabels", new ArrayList<>(especiesMap.keySet()));
        model.addAttribute("especiesData",   new ArrayList<>(especiesMap.values()));

        // Gráfico: comprobantes por tipo
        Map<String, Long> tiposMap = reporteService.getComprobantesPorTipo(comprobantes);
        model.addAttribute("tiposLabels", new ArrayList<>(tiposMap.keySet()));
        model.addAttribute("tiposData",   new ArrayList<>(tiposMap.values()));
        model.addAttribute("desde", desde);
        model.addAttribute("hasta", hasta);

        model.addAttribute("contenido", "reportes/dashboard");
        return "layout/base";
    }

    // Genera y descarga el reporte de facturación en Excel
    @GetMapping("/excel")
    public ResponseEntity<byte[]> exportarExcel(@RequestParam LocalDate desde, @RequestParam LocalDate hasta) {
        byte[] archivo = reporteExportService.crearExcel(reporteService.getComprobantes(desde, hasta), desde, hasta);
        return archivo(archivo, "reporte-facturacion.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

    // Genera y descarga el reporte de facturación en PDF
    @GetMapping("/pdf")
    public ResponseEntity<byte[]> exportarPdf(@RequestParam LocalDate desde, @RequestParam LocalDate hasta) {
        byte[] archivo = reporteExportService.crearPdf(reporteService.getComprobantes(desde, hasta), desde, hasta);
        return archivo(archivo, "reporte-facturacion.pdf", MediaType.APPLICATION_PDF_VALUE);
    }

    private ResponseEntity<byte[]> archivo(byte[] contenido, String nombre, String tipo) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + nombre)
                .contentType(MediaType.parseMediaType(tipo))
                .body(contenido);
    }
}
