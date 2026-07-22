package com.example.GestionVeterinaria.entity;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

// Entidad de acceso al sistema: representa las credenciales y el rol de quien inicia sesión
// Implementa UserDetails para que Spring Security pueda autenticar y autorizar directamente con esta entidad
@Entity
public class Usuarios implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Username único: se usa como identificador de login (ver UsuarioRepository.findByUsername)
    @Column(length = 100, nullable = false, unique = true)
    private String username;

    // Contraseña almacenada ya cifrada con PasswordEncoder (nunca en texto plano)
    @Column(length = 100, nullable = false)
    private String password;

    // Rol del usuario: "ADMIN", "VETERINARIO" o "RECEPCION"; define los permisos (@PreAuthorize) en los controladores
    @Column(length = 50, nullable = false)
    private String rol;

    // Solo se completa cuando el usuario tiene rol VETERINARIO; enlaza la cuenta de acceso con su ficha de veterinario
    @OneToOne
    @JoinColumn(name = "veterinario_id")
    private Veterinario veterinario;

    public Usuarios() {}

    public Usuarios(Long id, String username, String password, String rol, Veterinario veterinario) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.rol = rol;
        this.veterinario = veterinario;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    @Override
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    @Override
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public Veterinario getVeterinario() { return veterinario; }
    public void setVeterinario(Veterinario veterinario) { this.veterinario = veterinario; }

    // Convierte el campo "rol" al formato que exige Spring Security (prefijo "ROLE_") para usarlo en hasRole(...)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + rol));
    }

    // Los métodos siguientes son parte del contrato de UserDetails; se fijan en true porque el sistema
    // no maneja expiración de cuentas, bloqueo ni expiración de credenciales
    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
