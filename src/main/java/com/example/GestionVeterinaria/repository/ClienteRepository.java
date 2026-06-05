package com.example.GestionVeterinaria.repository;

import com.example.GestionVeterinaria.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente,Long> {
    //buscar cliente por id
    Cliente findByDni(String dni);

    //Verificar si existe el dni
    boolean existsByDni(String dni);


}
