package com.example.GestionVeterinaria.repository;

import com.example.GestionVeterinaria.entity.Comprobante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComprobanteRepository extends JpaRepository<Comprobante, Long> {

    // Busca comprobantes por id de cliente
    List<Comprobante> findByClienteId(Long clienteId);

    // Busca comprobantes por tipo (boleta o factura)
    List<Comprobante> findByTipo(String tipo);
}
