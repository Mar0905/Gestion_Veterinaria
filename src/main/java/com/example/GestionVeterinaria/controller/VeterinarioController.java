package com.example.GestionVeterinaria.controller;

import com.example.GestionVeterinaria.entity.Cita;
import com.example.GestionVeterinaria.entity.Usuarios;
import com.example.GestionVeterinaria.entity.Veterinario;
import com.example.GestionVeterinaria.repository.CitaRepository;
import com.example.GestionVeterinaria.repository.UsuarioRepository;
import com.example.GestionVeterinaria.service.VeterinarioService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.time.LocalDate;
import java.util.Comparator;

@Controller
@RequestMapping("veterinarios")
@PreAuthorize("hasRole('VETERINARIO')")
public class VeterinarioController
{

    private final VeterinarioService veterinarioService;
    private final CitaRepository citaRepository;
    private final UsuarioRepository usuarioRepository;

    public VeterinarioController(VeterinarioService veterinarioService, CitaRepository citaRepository, UsuarioRepository usuarioRepository) {
        this.veterinarioService = veterinarioService;
        this.citaRepository = citaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // Lista todos los veterinarios registrados
    @GetMapping
    public String listarVeterinarios(Model model){
        model.addAttribute("veterinarios",veterinarioService.listarTodos());
        model.addAttribute("veterinarioService",veterinarioService);
        model.addAttribute("contenido","veterinarios/listar");
        return "layout/base";
    }
    // Muestra el formulario para registrar un nuevo veterinario
    @GetMapping("/nuevo")
    public  String nuevoVeterinario(Model model){
        model.addAttribute("veterinario",new Veterinario());
        model.addAttribute("contenido", "veterinarios/formulario");
        return "layout/base";
    }

    // Registra un nuevo veterinario
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Veterinario veterinario){
        veterinarioService.registraVeterinario(veterinario);
        return "redirect:/veterinarios";
    }
    // Muestra la agenda de citas programadas del veterinario autenticado
    @GetMapping("/agenda")
    public String verAgenda(Model model, Authentication authentication) {

        String username = authentication.getName();

        Usuarios usuario = usuarioRepository.findByUsername(username)
                .orElseThrow();

        Long veterinarioId = usuario.getVeterinario().getId();

        // 🔥 SOLO citas programadas
        List<Cita> citas = citaRepository
                .findByVeterinarioIdAndEstado(veterinarioId, "Programada").stream()
                .sorted(Comparator.comparing(Cita::getFechaCita).thenComparing(Cita::getHoraCita))
                .toList();

        model.addAttribute("citas", citas);
        model.addAttribute("contenido", "veterinario/agenda");
        return "layout/base";
    }

    // Muestra el panel del veterinario autenticado con citas de hoy y estadísticas
    @GetMapping("/panel")
    public String panelVeterinario(Model model, Authentication authentication) {
        Usuarios usuario = usuarioRepository.findByUsername(authentication.getName()).orElseThrow();
        Veterinario veterinario = usuario.getVeterinario();
        if (veterinario == null) throw new IllegalStateException("El usuario no tiene un veterinario asociado");

        List<Cita> pendientes = citaRepository.findByVeterinarioIdAndEstado(veterinario.getId(), "Programada");
        List<Cita> citasHoy = pendientes.stream()
                .filter(c -> LocalDate.now().equals(c.getFechaCita()))
                .sorted(Comparator.comparing(Cita::getHoraCita)).toList();
        long atendidas = citaRepository.findByVeterinarioIdAndEstado(veterinario.getId(), "COMPLETADA").size();

        model.addAttribute("veterinario", veterinario);
        model.addAttribute("citasHoy", citasHoy);
        model.addAttribute("pendientes", pendientes.size());
        model.addAttribute("atendidas", atendidas);
        model.addAttribute("contenido", "veterinario/panel");
        return "layout/base";
    }

    // Lista las citas ya completadas por el veterinario autenticado
    @GetMapping("/atendidas")
    public String verAtendidas(Model model, Authentication authentication) {
        String username = authentication.getName();
        Usuarios usuario = usuarioRepository.findByUsername(username).orElseThrow();
        Long veterinarioId = usuario.getVeterinario().getId();

        List<Cita> citas = citaRepository
                .findByVeterinarioIdAndEstado(veterinarioId, "COMPLETADA");

        model.addAttribute("citas", citas);
        model.addAttribute("contenido", "veterinario/atendidas");
        return "layout/base";
    }



}
