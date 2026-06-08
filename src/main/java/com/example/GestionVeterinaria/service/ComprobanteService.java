package com.example.GestionVeterinaria.service;

import com.example.GestionVeterinaria.entity.Cliente;
import com.example.GestionVeterinaria.entity.Comprobante;
import com.example.GestionVeterinaria.entity.DetalleComprobante;
import com.example.GestionVeterinaria.entity.Producto;
import com.example.GestionVeterinaria.repository.ClienteRepository;
import com.example.GestionVeterinaria.repository.ComprobanteRepository;
import com.example.GestionVeterinaria.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ComprobanteService {

    private static final BigDecimal IGV = new BigDecimal("0.18");

    private final ComprobanteRepository comprobanteRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;

    public ComprobanteService(ComprobanteRepository comprobanteRepository,
                               ClienteRepository clienteRepository,
                               ProductoRepository productoRepository) {
        this.comprobanteRepository = comprobanteRepository;
        this.clienteRepository = clienteRepository;
        this.productoRepository = productoRepository;
    }

    public List<Comprobante> listarTodos() {
        return comprobanteRepository.findAll();
    }

    public Comprobante buscarPorId(Long id) {
        return comprobanteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comprobante no encontrado: " + id));
    }

    @Transactional
    public Comprobante crear(Long clienteId, String tipo, String metodoPago,
                              String[] productoIds, String[] descripciones,
                              String[] cantidades, String[] precios) {

        if (descripciones == null || descripciones.length == 0) {
            throw new RuntimeException("Debe agregar al menos una línea de detalle");
        }

        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        Comprobante comp = new Comprobante();
        comp.setCliente(cliente);
        comp.setTipo(tipo);
        comp.setMetodoPago(metodoPago);
        comp.setFecha(LocalDateTime.now());

        List<DetalleComprobante> detalles = new ArrayList<>();
        BigDecimal subtotalTotal = BigDecimal.ZERO;

        for (int i = 0; i < descripciones.length; i++) {
            String desc = descripciones[i].trim();
            if (desc.isEmpty()) continue;

            int cant = Integer.parseInt(cantidades[i]);
            BigDecimal precioU = new BigDecimal(precios[i]).setScale(2, RoundingMode.HALF_UP);
            BigDecimal subtDet = precioU.multiply(BigDecimal.valueOf(cant)).setScale(2, RoundingMode.HALF_UP);

            DetalleComprobante det = new DetalleComprobante();
            det.setComprobante(comp);
            det.setDescripcion(desc);
            det.setCantidad(cant);
            det.setPrecioUnitario(precioU);
            det.setSubtotal(subtDet);

            if (productoIds != null && i < productoIds.length
                    && productoIds[i] != null && !productoIds[i].isEmpty()) {
                Long prodId = Long.parseLong(productoIds[i]);
                productoRepository.findById(prodId).ifPresent(det::setProducto);
            }

            subtotalTotal = subtotalTotal.add(subtDet);
            detalles.add(det);
        }

        if (detalles.isEmpty()) {
            throw new RuntimeException("Debe agregar al menos una línea válida");
        }

        BigDecimal igv = subtotalTotal.multiply(IGV).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotalTotal.add(igv).setScale(2, RoundingMode.HALF_UP);

        comp.setSubtotal(subtotalTotal.setScale(2, RoundingMode.HALF_UP));
        comp.setIgv(igv);
        comp.setTotal(total);
        comp.setDetalles(detalles);

        return comprobanteRepository.save(comp);
    }

    public void eliminar(Long id) {
        comprobanteRepository.deleteById(id);
    }
}
