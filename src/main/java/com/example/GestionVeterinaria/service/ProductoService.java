package com.example.GestionVeterinaria.service;

import com.example.GestionVeterinaria.entity.Producto;
import com.example.GestionVeterinaria.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    public Producto buscarPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + id));
    }

    public Producto guardar(Producto producto) {
        if (producto.getStock() == null) producto.setStock(0);
        if (producto.getStockMinimo() == null) producto.setStockMinimo(5);
        return productoRepository.save(producto);
    }

    public Producto actualizar(Long id, Producto datos) {
        Producto existente = buscarPorId(id);
        existente.setNombre(datos.getNombre());
        existente.setCategoria(datos.getCategoria());
        existente.setPrecio(datos.getPrecio());
        existente.setStock(datos.getStock());
        existente.setStockMinimo(datos.getStockMinimo());
        existente.setFechaVencimiento(datos.getFechaVencimiento());
        return productoRepository.save(existente);
    }

    public void eliminar(Long id) {
        productoRepository.deleteById(id);
    }

    public List<Producto> listarStockBajo() {
        return productoRepository.findStockBajo();
    }

    public List<Producto> listarProximosVencer() {
        return productoRepository.findProximosVencer(LocalDate.now().plusDays(30));
    }

    public long contarStockBajo() {
        return productoRepository.findStockBajo().size();
    }

    public long contarProximosVencer() {
        return productoRepository.findProximosVencer(LocalDate.now().plusDays(30)).size();
    }

    public void registrarMovimiento(Long productoId, String tipo, Integer cantidad) {
        if (cantidad == null || cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
        }
        Producto p = buscarPorId(productoId);
        if ("entrada".equals(tipo)) {
            p.setStock(p.getStock() + cantidad);
        } else if ("salida".equals(tipo)) {
            if (cantidad > p.getStock()) {
                throw new IllegalArgumentException("Stock insuficiente para registrar la salida de " + p.getNombre());
            }
            p.setStock(p.getStock() - cantidad);
        } else {
            throw new IllegalArgumentException("Tipo de movimiento no válido");
        }
        productoRepository.save(p);
    }
}
