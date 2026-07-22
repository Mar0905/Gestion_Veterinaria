package com.example.GestionVeterinaria.controller;

import com.example.GestionVeterinaria.entity.Cita;
import com.example.GestionVeterinaria.entity.Comprobante;
import com.example.GestionVeterinaria.entity.Producto;
import com.example.GestionVeterinaria.repository.CitaRepository;
import com.example.GestionVeterinaria.repository.ClienteRepository;
import com.example.GestionVeterinaria.repository.ComprobanteRepository;
import com.example.GestionVeterinaria.repository.MascotaRepository;
import com.example.GestionVeterinaria.repository.ProductoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

// Controlador del dashboard principal (KPIs, próximas citas, alertas) y del panel de recepción
@Controller
public class HomeController {

    private final CitaRepository citaRepository;
    private final ClienteRepository clienteRepository;
    private final MascotaRepository mascotaRepository;
    private final ProductoRepository productoRepository;
    private final ComprobanteRepository comprobanteRepository;

    public HomeController(CitaRepository citaRepository,
                          ClienteRepository clienteRepository,
                          MascotaRepository mascotaRepository,
                          ProductoRepository productoRepository,
                          ComprobanteRepository comprobanteRepository) {
        this.citaRepository = citaRepository;
        this.clienteRepository = clienteRepository;
        this.mascotaRepository = mascotaRepository;
        this.productoRepository = productoRepository;
        this.comprobanteRepository = comprobanteRepository;
    }

    // Muestra la página de inicio de sesión
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // Muestra el dashboard principal con KPIs, próximas citas y alertas
    @GetMapping("/")
    public String home(Model model) {
        LocalDate hoy = LocalDate.now();

        // KPI: citas de hoy
        List<Cita> citasHoy = citaRepository.findByFechaCita(hoy);
        long atendidosHoy = citasHoy.stream()
                .filter(c -> "COMPLETADA".equalsIgnoreCase(c.getEstado()))
                .count();

        // KPI: total clientes
        long totalClientes = clienteRepository.count();

        // KPI: ingresos hoy
        BigDecimal ingresosHoy = comprobanteRepository.findAll().stream()
                .filter(c -> c.getFecha() != null && c.getFecha().toLocalDate().equals(hoy))
                .map(Comprobante::getTotal)
                .filter(t -> t != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Próximas citas (programadas, hoy o futuras, máx 6)
        List<Cita> proximasCitas = citaRepository.findByEstado("Programada").stream()
                .filter(c -> !c.getFechaCita().isBefore(hoy))
                .sorted(Comparator.comparing(Cita::getFechaCita)
                        .thenComparing(Cita::getHoraCita))
                .limit(6)
                .collect(Collectors.toList());

        // Alertas stock bajo (máx 3)
        List<Producto> alertasStock = productoRepository.findStockBajo().stream()
                .limit(3)
                .collect(Collectors.toList());

        // Alertas vencimiento próximo (máx 3)
        List<Producto> alertasVencimiento = productoRepository
                .findProximosVencer(hoy.plusDays(30)).stream()
                .limit(3)
                .collect(Collectors.toList());

        model.addAttribute("citasHoy", citasHoy.size());
        model.addAttribute("atendidosHoy", (int) atendidosHoy);
        model.addAttribute("totalClientes", totalClientes);
        model.addAttribute("ingresosHoy", ingresosHoy);
        model.addAttribute("proximasCitas", proximasCitas);
        model.addAttribute("alertasStock", alertasStock);
        model.addAttribute("alertasVencimiento", alertasVencimiento);
        model.addAttribute("hayAlertas",
                !alertasStock.isEmpty() || !alertasVencimiento.isEmpty());

        model.addAttribute("contenido", "home");
        return "layout/base";
    }

    // Lista todas las mascotas registradas en el sistema
    @GetMapping("/mascotas")
    public String todasMascotas(Model model) {
        model.addAttribute("mascotas", mascotaRepository.findAll());
        model.addAttribute("contenido", "mascotas/todas");
        return "layout/base";
    }

    // Muestra el panel de recepción con citas del día e ingresos
    @GetMapping("/recepcion")
    public String panelRecepcion(Model model) {
        LocalDate hoy = LocalDate.now();
        List<Cita> citasHoy = citaRepository.findByFechaCita(hoy);
        List<Cita> proximasCitas = citaRepository.findByEstado("Programada").stream()
                .filter(c -> !c.getFechaCita().isBefore(hoy))
                .sorted(Comparator.comparing(Cita::getFechaCita).thenComparing(Cita::getHoraCita))
                .limit(5).collect(Collectors.toList());
        BigDecimal ingresosHoy = comprobanteRepository.findAll().stream()
                .filter(c -> c.getFecha() != null && c.getFecha().toLocalDate().equals(hoy))
                .map(Comprobante::getTotal).filter(t -> t != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("citasHoy", citasHoy.size());
        model.addAttribute("clientes", clienteRepository.count());
        model.addAttribute("ingresosHoy", ingresosHoy);
        model.addAttribute("proximasCitas", proximasCitas);
        model.addAttribute("alertasStock", productoRepository.findStockBajo().stream().limit(4).toList());
        model.addAttribute("contenido", "recepcion/panel");
        return "layout/base";
    }
}
