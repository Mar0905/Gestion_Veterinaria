package com.example.GestionVeterinaria.service;

import com.example.GestionVeterinaria.entity.Comprobante;
import com.example.GestionVeterinaria.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

// Servicio que calcula los KPIs y los datos agregados usados en los gráficos del dashboard de reportes
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

    // Cuenta el total de clientes registrados
    public long getTotalClientes() {
        return clienteRepository.count();
    }

    // Cuenta el total de mascotas registradas
    public long getTotalMascotas() {
        return mascotaRepository.count();
    }

    // Cuenta el total de veterinarios registrados
    public long getTotalVeterinarios() {
        return veterinarioRepository.count();
    }

    // Cuenta las citas con estado "Programada"
    public long getCitasProgramadas() {
        return citaRepository.findByEstado("Programada").size();
    }

    // Cuenta los comprobantes emitidos en el mes actual
    public long getComprobantesDelMes() {
        YearMonth mes = YearMonth.now();
        LocalDateTime ini = mes.atDay(1).atStartOfDay();
        LocalDateTime fin = mes.atEndOfMonth().atTime(23, 59, 59);
        return comprobanteRepository.findAll().stream()
                .filter(c -> !c.getFecha().isBefore(ini) && !c.getFecha().isAfter(fin))
                .count();
    }

    // Suma los ingresos por comprobantes emitidos en el mes actual
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

    // Cuenta los productos con stock bajo
    public long getProductosStockBajo() {
        return productoRepository.findStockBajo().size();
    }

    // Lista los comprobantes emitidos dentro del rango de fechas indicado
    public List<Comprobante> getComprobantes(LocalDate desde, LocalDate hasta) {
        LocalDateTime inicio = desde.atStartOfDay();
        LocalDateTime fin = hasta.atTime(23, 59, 59);
        return comprobanteRepository.findAll().stream()
                .filter(c -> c.getFecha() != null && !c.getFecha().isBefore(inicio) && !c.getFecha().isAfter(fin))
                .sorted(Comparator.comparing(Comprobante::getFecha).reversed())
                .toList();
    }

    // Suma el total de una lista de comprobantes
    public BigDecimal getIngresos(List<Comprobante> comprobantes) {
        return comprobantes.stream().map(Comprobante::getTotal)
                .filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    // ── Datos para gráficos ─────────────────────────────────────────────────

    // Agrupa el conteo de citas por estado (sin filtro de fechas)
    public Map<String, Long> getCitasPorEstado() {
        return getCitasPorEstado(null, null);
    }

    // Agrupa el conteo de citas por estado dentro de un rango de fechas
    public Map<String, Long> getCitasPorEstado(LocalDate desde, LocalDate hasta) {
        var citas = citaRepository.findAll().stream()
                .filter(c -> desde == null || !c.getFechaCita().isBefore(desde))
                .filter(c -> hasta == null || !c.getFechaCita().isAfter(hasta))
                .toList();
        Map<String, Long> map = new LinkedHashMap<>();
        map.put("Programada", citas.stream().filter(c -> "Programada".equalsIgnoreCase(c.getEstado())).count());
        map.put("Atendidas", citas.stream().filter(c -> "COMPLETADA".equalsIgnoreCase(c.getEstado())).count());
        map.put("Cancelada", citas.stream().filter(c -> "Cancelada".equalsIgnoreCase(c.getEstado())).count());
        return map;
    }

    // Genera las etiquetas de los últimos 6 meses
    public List<String> getLabelsMeses() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM yyyy", Locale.of("es", "PE"));
        List<String> labels = new ArrayList<>();
        for (int i = 5; i >= 0; i--) {
            labels.add(YearMonth.now().minusMonths(i).format(fmt));
        }
        return labels;
    }

    // Calcula el total de ingresos por cada uno de los últimos 6 meses
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

    // Agrupa los ingresos por día o por mes según la duración del rango indicado
    public Map<String, Double> getIngresosPorPeriodo(List<Comprobante> comprobantes, LocalDate desde, LocalDate hasta) {
        boolean diario = !desde.plusDays(45).isBefore(hasta);
        DateTimeFormatter formato = diario
                ? DateTimeFormatter.ofPattern("dd MMM", Locale.of("es", "PE"))
                : DateTimeFormatter.ofPattern("MMM yyyy", Locale.of("es", "PE"));
        Map<String, Double> resultado = new LinkedHashMap<>();
        for (LocalDate fecha = desde; !fecha.isAfter(hasta); fecha = diario ? fecha.plusDays(1) : fecha.withDayOfMonth(1).plusMonths(1)) {
            LocalDate inicio = diario ? fecha : fecha.withDayOfMonth(1);
            LocalDate fin = diario ? fecha : fecha.withDayOfMonth(fecha.lengthOfMonth());
            double total = comprobantes.stream()
                    .filter(c -> !c.getFecha().toLocalDate().isBefore(inicio) && !c.getFecha().toLocalDate().isAfter(fin))
                    .map(Comprobante::getTotal).filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add).doubleValue();
            resultado.put(inicio.format(formato), total);
        }
        return resultado;
    }

    // Agrupa el conteo de mascotas por especie
    public Map<String, Long> getMascotasPorEspecie() {
        return mascotaRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        m -> m.getEspecie() != null ? m.getEspecie() : "Otro",
                        LinkedHashMap::new,
                        Collectors.counting()
                ));
    }

    // Agrupa el conteo de todos los comprobantes por tipo (boleta o factura)
    public Map<String, Long> getComprobantesPorTipo() {
        return getComprobantesPorTipo(comprobanteRepository.findAll());
    }

    // Agrupa el conteo de una lista de comprobantes por tipo (boleta o factura)
    public Map<String, Long> getComprobantesPorTipo(List<Comprobante> comprobantes) {
        Map<String, Long> map = new LinkedHashMap<>();
        map.put("BOLETA", comprobantes.stream().filter(c -> "BOLETA".equalsIgnoreCase(c.getTipo())).count());
        map.put("FACTURA", comprobantes.stream().filter(c -> "FACTURA".equalsIgnoreCase(c.getTipo())).count());
        return map;
    }
}
