package com.example.GestionVeterinaria.repository;

import com.example.GestionVeterinaria.entity.Usuarios;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuarios, Long> {
    // Busca un usuario por username
    Optional<Usuarios> findByUsername(String username);
    // Verifica si existe un usuario con ese username
    boolean existsByUsername(String username);
}
