package com.example.GestionVeterinaria.repository;

import com.example.GestionVeterinaria.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Busca productos por categoría
    List<Producto> findByCategoria(String categoria);

    // Busca productos cuyo stock está en el mínimo o por debajo
    @Query("SELECT p FROM Producto p WHERE p.stock <= p.stockMinimo")
    List<Producto> findStockBajo();

    // Busca productos cuya fecha de vencimiento está dentro del límite indicado
    @Query("SELECT p FROM Producto p WHERE p.fechaVencimiento IS NOT NULL AND p.fechaVencimiento <= :limite")
    List<Producto> findProximosVencer(@Param("limite") LocalDate limite);
}
