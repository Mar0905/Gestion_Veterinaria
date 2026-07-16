package com.example.GestionVeterinaria.controller;

import com.example.GestionVeterinaria.entity.Usuarios;
import com.example.GestionVeterinaria.entity.Veterinario;
import com.example.GestionVeterinaria.repository.UsuarioRepository;
import com.example.GestionVeterinaria.service.CitaService;
import com.example.GestionVeterinaria.service.ClienteService;
import com.example.GestionVeterinaria.service.VeterinarioService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final ClienteService clienteService;
    private  final VeterinarioService veterinarioService;
    private  final CitaService citaService;
    private final PasswordEncoder passwordEncoder;
    private  final UsuarioRepository usuarioRepository;


    public AdminController(ClienteService clienteService, VeterinarioService veterinarioService, CitaService citaService, PasswordEncoder passwordEncoder, UsuarioRepository usuarioRepository) {
        this.clienteService = clienteService;
        this.veterinarioService = veterinarioService;
        this.citaService = citaService;
        this.passwordEncoder = passwordEncoder;
        this.usuarioRepository = usuarioRepository;
    }
    // Muestra el panel de administración con clientes, veterinarios y citas
    @GetMapping
    public String panelAdmin(Model model){
        model.addAttribute("clientes", clienteService.listarTodos());
        model.addAttribute("veterinarios", veterinarioService.listarTodos());
        model.addAttribute("citas", citaService.listarTodo());
        model.addAttribute("contenido", "admin/panel");
        return "layout/base";
    }

    // Elimina un cliente por id
    @GetMapping("/eliminarCliente/{id}")
    public String eliminarCliente(@PathVariable Long id){
       clienteService.eliminar(id);
        return "redirect:/admin";
    }
    // Elimina una cita por id
    @GetMapping("/eliminarCita/{id}")
    public  String eliminarCita(@PathVariable Long id){
        citaService.eliminarCita(id);
        return "redirect:/admin";
    }
    // Elimina un veterinario por id
    @GetMapping("/eliminarVeterinario/{id}")
    public  String eliminarVeterinario(@PathVariable Long id){
    veterinarioService.eliminarVeterinario(id);
        return "redirect:/admin";
    }


    // Muestra el formulario para crear un nuevo veterinario
    @GetMapping("/veterinarios/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("veterinario", new Veterinario());
        model.addAttribute("contenido", "admin/crear_veterinario");
        return "layout/base";
    }

    // Registra un nuevo veterinario junto con su usuario de acceso
    @PostMapping("/veterinarios/guardar")
    public String guardarVeterinario(@ModelAttribute Veterinario veterinario,
                                     @RequestParam String username,
                                     @RequestParam String password) {

        Veterinario veterinarioGuardado = veterinarioService.registraVeterinario(veterinario);

        Usuarios usuarios = new Usuarios();
        usuarios.setUsername(username);
        usuarios.setPassword(passwordEncoder.encode(password));
        usuarios.setRol("VETERINARIO");
        usuarios.setVeterinario(veterinarioGuardado);
        usuarioRepository.save(usuarios);
        return "redirect:/admin";
    }

    // Muestra el formulario de edición de un veterinario existente
    @GetMapping("/veterinarios/{id}/editar")
    public String editarVeterinario(@PathVariable Long id, Model model) {
        model.addAttribute("veterinario", veterinarioService.buscarId(id));
        model.addAttribute("contenido", "admin/editar_veterinario");
        return "layout/base";
    }

    // Actualiza los datos de un veterinario existente
    @PostMapping("/veterinarios/{id}/actualizar")
    public String actualizarVeterinario(@PathVariable Long id,
                                         @ModelAttribute Veterinario veterinario) {
        veterinarioService.actualizar(id, veterinario);
        return "redirect:/admin";
    }



}
