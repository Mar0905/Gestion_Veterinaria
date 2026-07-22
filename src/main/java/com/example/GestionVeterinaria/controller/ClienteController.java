package com.example.GestionVeterinaria.controller;

import com.example.GestionVeterinaria.entity.Cliente;
import com.example.GestionVeterinaria.service.ClienteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// Controlador CRUD para el registro, edición, listado y eliminación de clientes
@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    // Lista todos los clientes registrados
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("clientes", clienteService.listarTodos());
        model.addAttribute("contenido", "clientes/listar");
        return "layout/base";
    }

    // Muestra el formulario para registrar un nuevo cliente
    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("cliente", new Cliente());
        model.addAttribute("contenido", "clientes/formulario");
        return "layout/base";
    }

    // Registra un nuevo cliente y valida DNI duplicado
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

    // Muestra el formulario de edición de un cliente existente
    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("cliente", clienteService.devolverCliente_id(id));
        model.addAttribute("contenido", "clientes/formulario");
        return "layout/base";
    }

    // Actualiza los datos de un cliente existente
    @PostMapping("/{id}/actualizar")
    public String actualizar(@PathVariable Long id,
                              @ModelAttribute Cliente cliente,
                              RedirectAttributes ra) {
        clienteService.actualizar(id, cliente);
        ra.addFlashAttribute("exito", "Cliente actualizado correctamente");
        return "redirect:/clientes";
    }

    // Elimina un cliente si no tiene mascotas asociadas
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
