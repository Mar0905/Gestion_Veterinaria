package com.example.GestionVeterinaria.controller;

import com.example.GestionVeterinaria.entity.Cita;
import com.example.GestionVeterinaria.entity.HistorialClinico;
import com.example.GestionVeterinaria.repository.CitaRepository;
import com.example.GestionVeterinaria.repository.HistorialClinicoRepository;
import com.example.GestionVeterinaria.repository.UsuarioRepository;
import com.example.GestionVeterinaria.entity.Usuarios;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/historial")
public class HistorialController {

    private final CitaRepository citaRepository;
    private final HistorialClinicoRepository historialRepository;
    private final UsuarioRepository usuarioRepository;

    public HistorialController(CitaRepository citaRepository,
                               HistorialClinicoRepository historialRepository,
                               UsuarioRepository usuarioRepository) {
        this.citaRepository = citaRepository;
        this.historialRepository = historialRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // Lista el historial clínico, filtrado por veterinario si aplica
    @GetMapping
    public String listarHistorial(Model model, Authentication authentication) {
        List<HistorialClinico> historiales = historialRepository.findAll();
        if (esVeterinario(authentication)) {
            Long veterinarioId = veterinarioActual(authentication).getId();
            historiales = historiales.stream()
                    .filter(h -> h.getCita() != null && h.getCita().getVeterinario().getId().equals(veterinarioId))
                    .toList();
        }
        model.addAttribute("historiales", historiales);
        model.addAttribute("vistaVeterinario", esVeterinario(authentication));
        model.addAttribute("contenido", "historial/listar");
        return "layout/base";
    }

    // Muestra el formulario para registrar la atención de una cita
    @GetMapping("/crear/{id}")
    public String mostrarFormulario(@PathVariable Long id, Model model, Authentication authentication) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));
        validarCitaAsignada(cita, authentication);
        if (cita.getHistorialClinico() != null) {
            throw new IllegalStateException("La cita ya tiene una atención registrada");
        }

        HistorialClinico historial = new HistorialClinico();
        historial.setMascota(cita.getMascota());

        model.addAttribute("historial", historial);
        model.addAttribute("cita", cita);
        model.addAttribute("citaId", id);
        model.addAttribute("contenido", "historial/formulario");
        return "layout/base";
    }

    // Registra el historial clínico de una cita y la marca como completada
    @PostMapping("/guardar")
    public String guardarHistorial(@ModelAttribute HistorialClinico historial,
                                   @RequestParam Long citaId,
                                   Authentication authentication) {
        Cita cita = citaRepository.findById(citaId).orElseThrow();
        validarCitaAsignada(cita, authentication);
        if (cita.getHistorialClinico() != null) {
            throw new IllegalStateException("La cita ya tiene una atención registrada");
        }

        historial.setMascota(cita.getMascota());
        historial.setCita(cita);
        historial.setFechaConsulta(LocalDate.now());

        cita.setEstado("COMPLETADA");

        historialRepository.save(historial);
        citaRepository.save(cita);

        return "redirect:/veterinarios/agenda";
    }

    private void validarCitaAsignada(Cita cita, Authentication authentication) {
        if (!esVeterinario(authentication)) {
            throw new AccessDeniedException("Solo el veterinario asignado puede registrar la atención");
        }
        if (!cita.getVeterinario().getId().equals(veterinarioActual(authentication).getId())) {
            throw new AccessDeniedException("No puedes registrar una atención de otro veterinario");
        }
    }

    private boolean esVeterinario(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_VETERINARIO".equals(a.getAuthority()));
    }

    private com.example.GestionVeterinaria.entity.Veterinario veterinarioActual(Authentication authentication) {
        Usuarios usuario = usuarioRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new AccessDeniedException("Usuario no encontrado"));
        if (usuario.getVeterinario() == null) {
            throw new AccessDeniedException("El usuario no tiene un veterinario asociado");
        }
        return usuario.getVeterinario();
    }
}
