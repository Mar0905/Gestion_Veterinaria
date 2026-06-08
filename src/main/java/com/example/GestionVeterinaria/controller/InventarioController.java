package com.example.GestionVeterinaria.controller;

import com.example.GestionVeterinaria.entity.Producto;
import com.example.GestionVeterinaria.service.ProductoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/inventario")
public class InventarioController {

    private final ProductoService productoService;

    public InventarioController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("productos", productoService.listarTodos());
        model.addAttribute("alertasStock", productoService.contarStockBajo());
        model.addAttribute("alertasVencimiento", productoService.contarProximosVencer());
        model.addAttribute("contenido", "inventario/lista");
        return "layout/base";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("producto", new Producto());
        model.addAttribute("contenido", "inventario/formulario");
        return "layout/base";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Producto producto) {
        productoService.guardar(producto);
        return "redirect:/inventario";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("producto", productoService.buscarPorId(id));
        model.addAttribute("contenido", "inventario/formulario");
        return "layout/base";
    }

    @PostMapping("/{id}/actualizar")
    public String actualizar(@PathVariable Long id, @ModelAttribute Producto producto) {
        productoService.actualizar(id, producto);
        return "redirect:/inventario";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
        return "redirect:/inventario";
    }
}
