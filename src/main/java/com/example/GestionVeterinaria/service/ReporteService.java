package com.example.GestionVeterinaria.service;

import com.example.GestionVeterinaria.entity.Comprobante;
import com.example.GestionVeterinaria.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReporteService {

    private final ClienteRepository clienteRepository;
    private final MascotaRepository mascotaRepository;
    private final VeterinarioRepository veterinarioRepository;
    private final CitaRepository citaRepository;
    private final ComprobanteRepository comprobanteRepository;
    private final ProductoRepository productoRepository;

    public ReporteService(ClienteRepository clienteRepository,
                          MascotaRepository mascotaRepository,
                          VeterinarioRepository veterinarioRepository,
                          CitaRepository citaRepository,
                          ComprobanteRepository comprobanteRepository,
                          ProductoRepository productoRepository) {
        this.clienteRepository = clienteRepository;
        this.mascotaRepository = mascotaRepository;
        this.veterinarioRepository = veterinarioRepository;
        this.citaRepository = citaRepository;
        this.comprobanteRepository = comprobanteRepository;
        this.productoRepository = productoRepository;
    }

    // ── KPIs ────────────────────────────────────────────────────────────────

    public long getTotalClientes() {
        return clienteRepository.count();
    }

    public long getTotalMascotas() {
        return mascotaRepository.count();
    }

    public long getTotalVeterinarios() {
        return veterinarioRepository.count();
    }

    public long getCitasProgramadas() {
        return citaRepository.findByEstado("Programada").size();
    }

    public long getComprobantesDelMes() {
        YearMonth mes = YearMonth.now();
        LocalDateTime ini = mes.atDay(1).atStartOfDay();
        LocalDateTime fin = mes.atEndOfMonth().atTime(23, 59, 59);
        return comprobanteRepository.findAll().stream()
                .filter(c -> !c.getFecha().isBefore(ini) && !c.getFecha().isAfter(fin))
                .count();
    }

    public BigDecimal getIngresosDelMes() {
        YearMonth mes = YearMonth.now();
        LocalDateTime ini = mes.atDay(1).atStartOfDay();
        LocalDateTime fin = mes.atEndOfMonth().atTime(23, 59, 59);
        return comprobanteRepository.findAll().stream()
                .filter(c -> !c.getFecha().isBefore(ini) && !c.getFecha().isAfter(fin))
                .map(Comprobante::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public long getProductosStockBajo() {
        return productoRepository.findStockBajo().size();
    }

    // ── Datos para gráficos ─────────────────────────────────────────────────

    public Map<String, Long> getCitasPorEstado() {
        Map<String, Long> map = new LinkedHashMap<>();
        map.put("Programada", (long) citaRepository.findByEstado("Programada").size());
        map.put("Atendida",   (long) citaRepository.findByEstado("Atendida").size());
        map.put("Cancelada",  (long) citaRepository.findByEstado("Cancelada").size());
        return map;
    }

    public List<String> getLabelsMeses() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM yyyy", Locale.of("es", "PE"));
        List<String> labels = new ArrayList<>();
        for (int i = 5; i >= 0; i--) {
            labels.add(YearMonth.now().minusMonths(i).format(fmt));
        }
        return labels;
    }

    public List<Double> getIngresosPorMes() {
        List<Comprobante> todos = comprobanteRepository.findAll();
        List<Double> totales = new ArrayList<>();
        for (int i = 5; i >= 0; i--) {
            YearMonth mes = YearMonth.now().minusMonths(i);
            LocalDateTime ini = mes.atDay(1).atStartOfDay();
            LocalDateTime fin = mes.atEndOfMonth().atTime(23, 59, 59);
            double t = todos.stream()
                    .filter(c -> !c.getFecha().isBefore(ini) && !c.getFecha().isAfter(fin))
                    .map(Comprobante::getTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .doubleValue();
            totales.add(t);
        }
        return totales;
    }

    public Map<String, Long> getMascotasPorEspecie() {
        return mascotaRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        m -> m.getEspecie() != null ? m.getEspecie() : "Otro",
                        LinkedHashMap::new,
                        Collectors.counting()
                ));
    }

    public Map<String, Long> getComprobantesPorTipo() {
        Map<String, Long> map = new LinkedHashMap<>();
        map.put("BOLETA",  (long) comprobanteRepository.findByTipo("BOLETA").size());
        map.put("FACTURA", (long) comprobanteRepository.findByTipo("FACTURA").size());
        return map;
    }
}
