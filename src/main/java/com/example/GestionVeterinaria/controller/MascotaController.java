package com.example.GestionVeterinaria.controller;
import com.example.GestionVeterinaria.entity.Mascota;
import org.springframework.ui.Model;
import com.example.GestionVeterinaria.entity.Cliente;
import com.example.GestionVeterinaria.service.ClienteService;
import com.example.GestionVeterinaria.service.MascotaService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/clientes/{clienteId}/mascotas")
public class MascotaController {

    private final MascotaService mascotaService;
    private final ClienteService clienteService;

    public MascotaController(MascotaService mascotaService,
                             ClienteService clienteService) {
        this.mascotaService = mascotaService;
        this.clienteService = clienteService;
    }

    // Listar mascotas del cliente
    @GetMapping
    public String listarMascotas(@PathVariable Long clienteId, Model model) {

        Cliente cliente = clienteService.devolverCliente_id(clienteId);

        model.addAttribute("cliente", cliente);
        model.addAttribute("mascotas",
                mascotaService.listarPorCliente(clienteId));
        model.addAttribute("contenido","mascotas/listar");

        return "layout/base";
    }

    // Mostrar formulario para registrar mascota
    @GetMapping("/nueva")
    public String mostrarFormulario(@PathVariable Long clienteId, Model model) {

        model.addAttribute("mascota", new Mascota());
        model.addAttribute("clienteId", clienteId);
        model.addAttribute("contenido","mascotas/formulario");
        return "layout/base";
    }

    // Guardar mascota
    @PostMapping("/guardar")
    public String guardarMascota(@PathVariable Long clienteId,
                                 @ModelAttribute Mascota mascota) {

        mascotaService.registrar(mascota, clienteId);

        return "redirect:/clientes/" + clienteId + "/mascotas";
    }



}
