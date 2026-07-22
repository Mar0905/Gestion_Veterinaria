package com.example.GestionVeterinaria.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

// Entidad que representa un producto del inventario de la veterinaria
@Entity
@Table(name = "Productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 150, nullable = false)
    private String nombre;

    @Column(length = 50, nullable = false)
    private String categoria;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(nullable = false)
    private Integer stock = 0;

    // Umbral bajo el cual se considera "stock bajo" y se muestra como alerta en el dashboard (isStockBajo)
    @Column(name = "stock_minimo", nullable = false)
    private Integer stockMinimo = 5;

    // Opcional; usada para las alertas de productos próximos a vencer o vencidos en el dashboard
    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    public Producto() {}

    public Producto(Long id, String nombre, String categoria, BigDecimal precio,
                    Integer stock, Integer stockMinimo, LocalDate fechaVencimiento) {
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
        this.precio = precio;
        this.stock = stock;
        this.stockMinimo = stockMinimo;
        this.fechaVencimiento = fechaVencimiento;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public Integer getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(Integer stockMinimo) { this.stockMinimo = stockMinimo; }

    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }

    // Indica si el stock actual está en el mínimo o por debajo
    public boolean isStockBajo() {
        return stock != null && stockMinimo != null && stock <= stockMinimo;
    }

    // Indica si el producto vence dentro de los próximos 30 días
    public boolean isProximoVencer() {
        if (fechaVencimiento == null) return false;
        return !fechaVencimiento.isAfter(LocalDate.now().plusDays(30));
    }

    // Indica si el producto ya venció
    public boolean isVencido() {
        if (fechaVencimiento == null) return false;
        return fechaVencimiento.isBefore(LocalDate.now());
    }
}
