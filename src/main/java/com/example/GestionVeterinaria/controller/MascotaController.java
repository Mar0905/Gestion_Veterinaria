package com.example.GestionVeterinaria.controller;

import com.example.GestionVeterinaria.entity.Mascota;
import com.example.GestionVeterinaria.service.ClienteService;
import com.example.GestionVeterinaria.service.MascotaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/clientes/{clienteId}/mascotas")
public class MascotaController {

    private final MascotaService mascotaService;
    private final ClienteService clienteService;

    public MascotaController(MascotaService mascotaService, ClienteService clienteService) {
        this.mascotaService = mascotaService;
        this.clienteService = clienteService;
    }

    @GetMapping
    public String listarMascotas(@PathVariable Long clienteId, Model model) {
        model.addAttribute("cliente", clienteService.devolverCliente_id(clienteId));
        model.addAttribute("mascotas", mascotaService.listarPorCliente(clienteId));
        model.addAttribute("contenido", "mascotas/listar");
        return "layout/base";
    }

    @GetMapping("/nueva")
    public String nueva(@PathVariable Long clienteId, Model model) {
        model.addAttribute("mascota", new Mascota());
        model.addAttribute("clienteId", clienteId);
        model.addAttribute("contenido", "mascotas/formulario");
        return "layout/base";
    }

    @PostMapping("/guardar")
    public String guardar(@PathVariable Long clienteId,
                           @ModelAttribute Mascota mascota,
                           RedirectAttributes ra) {
        mascotaService.registrar(mascota, clienteId);
        ra.addFlashAttribute("exito", "Mascota registrada correctamente");
        return "redirect:/clientes/" + clienteId + "/mascotas";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long clienteId,
                          @PathVariable Long id,
                          Model model) {
        model.addAttribute("mascota", mascotaService.buscarPorId(id));
        model.addAttribute("clienteId", clienteId);
        model.addAttribute("contenido", "mascotas/formulario");
        return "layout/base";
    }

    @PostMapping("/{id}/actualizar")
    public String actualizar(@PathVariable Long clienteId,
                              @PathVariable Long id,
                              @ModelAttribute Mascota mascota,
                              RedirectAttributes ra) {
        mascotaService.actualizar(id, mascota);
        ra.addFlashAttribute("exito", "Mascota actualizada correctamente");
        return "redirect:/clientes/" + clienteId + "/mascotas";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long clienteId,
                            @PathVariable Long id,
                            RedirectAttributes ra) {
        mascotaService.eliminarMascota(id);
        ra.addFlashAttribute("exito", "Mascota eliminada");
        return "redirect:/clientes/" + clienteId + "/mascotas";
    }
}
