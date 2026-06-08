package com.example.GestionVeterinaria.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Comprobantes")
public class Comprobante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(length = 10, nullable = false)
    private String tipo; // BOLETA, FACTURA

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal igv;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Column(name = "metodo_pago", length = 20, nullable = false)
    private String metodoPago; // efectivo, tarjeta, yape, plin

    @Column(nullable = false)
    private LocalDateTime fecha;

    @OneToMany(mappedBy = "comprobante", cascade = CascadeType.ALL)
    private List<DetalleComprobante> detalles;

    public Comprobante() {}

    public Comprobante(Long id, Cliente cliente, String tipo, BigDecimal subtotal,
                       BigDecimal igv, BigDecimal total, String metodoPago,
                       LocalDateTime fecha, List<DetalleComprobante> detalles) {
        this.id = id;
        this.cliente = cliente;
        this.tipo = tipo;
        this.subtotal = subtotal;
        this.igv = igv;
        this.total = total;
        this.metodoPago = metodoPago;
        this.fecha = fecha;
        this.detalles = detalles;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getIgv() { return igv; }
    public void setIgv(BigDecimal igv) { this.igv = igv; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public List<DetalleComprobante> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleComprobante> detalles) { this.detalles = detalles; }
}
