package com.example.GestionVeterinaria.repository;

import com.example.GestionVeterinaria.entity.Veterinario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VeterinarioRepository extends JpaRepository<Veterinario,Long> {
    // Elimina un veterinario por id
    void deleteById(Long id);
}
