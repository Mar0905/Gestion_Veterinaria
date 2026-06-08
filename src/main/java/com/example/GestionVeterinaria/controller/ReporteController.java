package com.example.GestionVeterinaria.controller;

import com.example.GestionVeterinaria.service.ReporteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.Map;

@Controller
@RequestMapping("/reportes")
public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping
    public String dashboard(Model model) {
        // KPIs
        model.addAttribute("totalClientes",     reporteService.getTotalClientes());
        model.addAttribute("totalMascotas",     reporteService.getTotalMascotas());
        model.addAttribute("totalVeterinarios", reporteService.getTotalVeterinarios());
        model.addAttribute("citasProgramadas",  reporteService.getCitasProgramadas());
        model.addAttribute("comprobantesDelMes",reporteService.getComprobantesDelMes());
        model.addAttribute("ingresosDelMes",    reporteService.getIngresosDelMes());
        model.addAttribute("stockBajo",         reporteService.getProductosStockBajo());

        // Gráfico: citas por estado
        Map<String, Long> citasMap = reporteService.getCitasPorEstado();
        model.addAttribute("citasLabels", new ArrayList<>(citasMap.keySet()));
        model.addAttribute("citasCounts", new ArrayList<>(citasMap.values()));

        // Gráfico: ingresos últimos 6 meses
        model.addAttribute("ingresosLabels", reporteService.getLabelsMeses());
        model.addAttribute("ingresosData",   reporteService.getIngresosPorMes());

        // Gráfico: mascotas por especie
        Map<String, Long> especiesMap = reporteService.getMascotasPorEspecie();
        model.addAttribute("especiesLabels", new ArrayList<>(especiesMap.keySet()));
        model.addAttribute("especiesData",   new ArrayList<>(especiesMap.values()));

        // Gráfico: comprobantes por tipo
        Map<String, Long> tiposMap = reporteService.getComprobantesPorTipo();
        model.addAttribute("tiposLabels", new ArrayList<>(tiposMap.keySet()));
        model.addAttribute("tiposData",   new ArrayList<>(tiposMap.values()));

        model.addAttribute("contenido", "reportes/dashboard");
        return "layout/base";
    }
}
