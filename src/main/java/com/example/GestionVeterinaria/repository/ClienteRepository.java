package com.example.GestionVeterinaria.repository;

import com.example.GestionVeterinaria.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Repositorio JPA para el acceso a datos de clientes
@Repository
public interface ClienteRepository extends JpaRepository<Cliente,Long> {
    // Busca un cliente por DNI
    Cliente findByDni(String dni);

    // Verifica si existe un cliente con ese DNI
    boolean existsByDni(String dni);


}
