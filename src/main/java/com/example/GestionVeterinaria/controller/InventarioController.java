package com.example.GestionVeterinaria.controller;

import com.example.GestionVeterinaria.entity.Producto;
import com.example.GestionVeterinaria.service.ProductoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/inventario")
public class InventarioController {

    private final ProductoService productoService;

    public InventarioController(ProductoService productoService) {
        this.productoService = productoService;
    }

    // Lista los productos de inventario con alertas de stock y vencimiento
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("productos", productoService.listarTodos());
        model.addAttribute("alertasStock", productoService.contarStockBajo());
        model.addAttribute("alertasVencimiento", productoService.contarProximosVencer());
        model.addAttribute("listaStockBajo", productoService.listarStockBajo());
        model.addAttribute("listaProximosVencer", productoService.listarProximosVencer());
        model.addAttribute("contenido", "inventario/lista");
        return "layout/base";
    }

    // Registra una entrada o salida de stock para un producto
    @PostMapping("/movimiento")
    public String registrarMovimiento(@RequestParam Long productoId,
                                      @RequestParam String tipo,
                                      @RequestParam Integer cantidad,
                                      RedirectAttributes ra) {
        try {
            productoService.registrarMovimiento(productoId, tipo, cantidad);
            ra.addFlashAttribute("exito", "Movimiento registrado correctamente");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/inventario";
    }

    // Muestra el formulario para registrar un nuevo producto
    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("producto", new Producto());
        model.addAttribute("contenido", "inventario/formulario");
        return "layout/base";
    }

    // Registra un nuevo producto en el inventario
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Producto producto) {
        productoService.guardar(producto);
        return "redirect:/inventario";
    }

    // Muestra el formulario de edición de un producto existente
    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("producto", productoService.buscarPorId(id));
        model.addAttribute("contenido", "inventario/formulario");
        return "layout/base";
    }

    // Actualiza los datos de un producto existente
    @PostMapping("/{id}/actualizar")
    public String actualizar(@PathVariable Long id, @ModelAttribute Producto producto) {
        productoService.actualizar(id, producto);
        return "redirect:/inventario";
    }

    // Elimina un producto del inventario
    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
        return "redirect:/inventario";
    }
}
