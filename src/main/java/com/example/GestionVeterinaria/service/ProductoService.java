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

    // Lista todos los productos del inventario
    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    // Busca un producto por id
    public Producto buscarPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + id));
    }

    // Registra un nuevo producto con valores por defecto de stock
    public Producto guardar(Producto producto) {
        if (producto.getStock() == null) producto.setStock(0);
        if (producto.getStockMinimo() == null) producto.setStockMinimo(5);
        return productoRepository.save(producto);
    }

    // Actualiza los datos de un producto existente
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

    // Elimina un producto por id
    public void eliminar(Long id) {
        productoRepository.deleteById(id);
    }

    // Lista los productos con stock igual o menor al mínimo
    public List<Producto> listarStockBajo() {
        return productoRepository.findStockBajo();
    }

    // Lista los productos próximos a vencer en los siguientes 30 días
    public List<Producto> listarProximosVencer() {
        return productoRepository.findProximosVencer(LocalDate.now().plusDays(30));
    }

    // Cuenta los productos con stock bajo
    public long contarStockBajo() {
        return productoRepository.findStockBajo().size();
    }

    // Cuenta los productos próximos a vencer en los siguientes 30 días
    public long contarProximosVencer() {
        return productoRepository.findProximosVencer(LocalDate.now().plusDays(30)).size();
    }

    // Registra una entrada o salida de stock validando disponibilidad
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
