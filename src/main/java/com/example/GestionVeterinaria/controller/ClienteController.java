package com.example.GestionVeterinaria.controller;

import com.example.GestionVeterinaria.entity.Cliente;
import com.example.GestionVeterinaria.service.ClienteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("clientes", clienteService.listarTodos());
        model.addAttribute("contenido", "clientes/listar");
        return "layout/base";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("cliente", new Cliente());
        model.addAttribute("contenido", "clientes/formulario");
        return "layout/base";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Cliente cliente, RedirectAttributes ra) {
        try {
            clienteService.registrar(cliente);
            ra.addFlashAttribute("exito", "Cliente registrado correctamente");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/clientes/nuevo";
        }
        return "redirect:/clientes";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("cliente", clienteService.devolverCliente_id(id));
        model.addAttribute("contenido", "clientes/formulario");
        return "layout/base";
    }

    @PostMapping("/{id}/actualizar")
    public String actualizar(@PathVariable Long id,
                              @ModelAttribute Cliente cliente,
                              RedirectAttributes ra) {
        clienteService.actualizar(id, cliente);
        ra.addFlashAttribute("exito", "Cliente actualizado correctamente");
        return "redirect:/clientes";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes ra) {
        try {
            clienteService.eliminar(id);
            ra.addFlashAttribute("exito", "Cliente eliminado");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "No se puede eliminar: el cliente tiene mascotas asociadas");
        }
        return "redirect:/clientes";
    }
}
