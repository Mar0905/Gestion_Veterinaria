package com.example.GestionVeterinaria.controller;

import com.example.GestionVeterinaria.service.ClienteService;
import com.example.GestionVeterinaria.service.ComprobanteService;
import com.example.GestionVeterinaria.service.ProductoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador encargado de gestionar el proceso de facturación.
 * Permite listar, crear, visualizar y eliminar comprobantes.
 */
@Controller
@RequestMapping("/facturacion")
public class FacturacionController {

    // Servicios utilizados por el controlador
    private final ComprobanteService comprobanteService;
    private final ClienteService clienteService;
    private final ProductoService productoService;

    /**
     * Constructor con inyección de dependencias.
     */
    public FacturacionController(ComprobanteService comprobanteService,
                                 ClienteService clienteService,
                                 ProductoService productoService) {
        this.comprobanteService = comprobanteService;
        this.clienteService = clienteService;
        this.productoService = productoService;
    }

    /**
     * Muestra la lista de comprobantes registrados.
     */
    @GetMapping
    public String listar(Model model) {
        // Obtiene todos los comprobantes
        model.addAttribute("comprobantes", comprobanteService.listarTodos());

        // Vista que se cargará dentro del layout principal
        model.addAttribute("contenido", "facturacion/lista");

        return "layout/base";
    }

    /**
     * Muestra el formulario para registrar un nuevo comprobante.
     */
    @GetMapping("/nuevo")
    public String nuevo(Model model) {

        // Carga la lista de clientes disponibles
        model.addAttribute("clientes", clienteService.listarTodos());

        // Carga la lista de productos disponibles
        model.addAttribute("productos", productoService.listarTodos());

        // Vista del formulario
        model.addAttribute("contenido", "facturacion/nuevo");

        return "layout/base";
    }

    /**
     * Guarda un nuevo comprobante con sus detalles.
     */
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
            // Crea el comprobante llamando al servicio
            var comp = comprobanteService.crear(
                    clienteId,
                    tipo,
                    metodoPago,
                    productoIds,
                    descripciones,
                    cantidades,
                    precios
            );

            // Mensaje de éxito
            ra.addFlashAttribute(
                    "exito",
                    "Comprobante #" + comp.getId() + " emitido correctamente"
            );

            // Redirige al listado de comprobantes
            return "redirect:/facturacion";

        } catch (Exception e) {

            // Si ocurre un error, muestra el mensaje correspondiente
            ra.addFlashAttribute("error", e.getMessage());

            // Regresa al formulario
            return "redirect:/facturacion/nuevo";
        }
    }

    /**
     * Muestra la información detallada de un comprobante.
     */
    @GetMapping("/{id}")
    public String ver(@PathVariable Long id, Model model) {

        // Busca el comprobante por su ID
        model.addAttribute("comprobante", comprobanteService.buscarPorId(id));

        // Vista de detalle
        model.addAttribute("contenido", "facturacion/ver");

        return "layout/base";
    }

    /**
     * Elimina un comprobante por su identificador.
     */
    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes ra) {

        // Elimina el comprobante
        comprobanteService.eliminar(id);

        // Mensaje de confirmación
        ra.addFlashAttribute("exito", "Comprobante eliminado");

        // Redirige al listado
        return "redirect:/facturacion";
    }
}
