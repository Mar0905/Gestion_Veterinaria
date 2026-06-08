package com.example.GestionVeterinaria.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class HttpSecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {

                    // Recursos públicos
                    auth.requestMatchers("/login", "/error",
                                    "/css/**", "/js/**", "/images/**")
                            .permitAll();

                    // ADMIN
                    auth.requestMatchers("/admin/**", "/inventario/**", "/reportes/**")
                            .hasRole("ADMIN");

                    // VETERINARIO
                    auth.requestMatchers("/veterinario/**")
                            .hasRole("VETERINARIO");

                    // Mascotas standalone (ADMIN y RECEPCION)
                    auth.requestMatchers("/mascotas")
                            .hasAnyRole("ADMIN", "RECEPCION");

                    // Historial (ADMIN, VETERINARIO y RECEPCION)
                    auth.requestMatchers("/historial", "/historial/**")
                            .hasAnyRole("ADMIN", "VETERINARIO", "RECEPCION");

                    // RECEPCION puede gestionar citas y facturación
                    auth.requestMatchers("/citas/**", "/facturacion/**")
                            .hasAnyRole("ADMIN", "RECEPCION");

                    // Todo lo demás requiere login
                    auth.anyRequest().authenticated();
                })
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/login?error")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(authenticationProvider);
    }

}
