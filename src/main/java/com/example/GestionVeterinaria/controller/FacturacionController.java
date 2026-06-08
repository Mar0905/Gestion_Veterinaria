package com.example.GestionVeterinaria.controller;

import com.example.GestionVeterinaria.service.ClienteService;
import com.example.GestionVeterinaria.service.ComprobanteService;
import com.example.GestionVeterinaria.service.ProductoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/facturacion")
public class FacturacionController {

    private final ComprobanteService comprobanteService;
    private final ClienteService clienteService;
    private final ProductoService productoService;

    public FacturacionController(ComprobanteService comprobanteService,
                                  ClienteService clienteService,
                                  ProductoService productoService) {
        this.comprobanteService = comprobanteService;
        this.clienteService = clienteService;
        this.productoService = productoService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("comprobantes", comprobanteService.listarTodos());
        model.addAttribute("contenido", "facturacion/lista");
        return "layout/base";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("clientes", clienteService.listarTodos());
        model.addAttribute("productos", productoService.listarTodos());
        model.addAttribute("contenido", "facturacion/nuevo");
        return "layout/base";
    }

    @PostMapping("/guardar")
    public String guardar(
            @RequestParam Long clienteId,
            @RequestParam String tipo,
            @RequestParam String metodoPago,
            @RequestParam(value = "productoId", required = false) String[] productoIds,
            @RequestParam("descripcion") String[] descripciones,
            @RequestParam("cantidad") String[] cantidades,
            @RequestParam("precioUnitario") String[] precios,
            RedirectAttributes ra) {
        try {
            var comp = comprobanteService.crear(clienteId, tipo, metodoPago,
                    productoIds, descripciones, cantidades, precios);
            ra.addFlashAttribute("exito", "Comprobante #" + comp.getId() + " emitido correctamente");
            return "redirect:/facturacion";
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/facturacion/nuevo";
        }
    }

    @GetMapping("/{id}")
    public String ver(@PathVariable Long id, Model model) {
        model.addAttribute("comprobante", comprobanteService.buscarPorId(id));
        model.addAttribute("contenido", "facturacion/ver");
        return "layout/base";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes ra) {
        comprobanteService.eliminar(id);
        ra.addFlashAttribute("exito", "Comprobante eliminado");
        return "redirect:/facturacion";
    }
}
