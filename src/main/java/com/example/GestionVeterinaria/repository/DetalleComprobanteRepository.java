package com.example.GestionVeterinaria.repository;

import com.example.GestionVeterinaria.entity.DetalleComprobante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Repositorio de detalles de comprobante (solo usa los métodos CRUD heredados de JpaRepository)
@Repository
public interface DetalleComprobanteRepository extends JpaRepository<DetalleComprobante, Long> {
}
