package com.example.GestionVeterinaria.repository;

import com.example.GestionVeterinaria.entity.Cliente;
import com.example.GestionVeterinaria.entity.Mascota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MascotaRepository extends JpaRepository<Mascota,Long> {
    // Busca mascotas por cliente
    List<Mascota> findByCliente (Cliente cliente);
    // Busca mascotas por id de cliente
    List<Mascota> findByCliente_id(Long cliente_id);

}
