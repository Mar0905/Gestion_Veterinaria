package com.example.GestionVeterinaria.config;

import com.example.GestionVeterinaria.entity.*;
import com.example.GestionVeterinaria.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalTime;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(UsuarioRepository usuarioRepository,
                               VeterinarioRepository veterinarioRepository,
                               ClienteRepository clienteRepository,
                               MascotaRepository mascotaRepository,
                               CitaRepository citaRepository,
                               HistorialClinicoRepository historialRepository,
                               PasswordEncoder passwordEncoder) {

        return args -> {

            // Solo ejecutar si la base de datos está completamente vacía
            if (usuarioRepository.count() > 0) return;

            // ── 1. Veterinario predeterminado ──────────────────────────
            Veterinario vet = new Veterinario();
            vet.setNombre("Carlos López");
            vet.setEspecialidad("Medicina General");
            vet.setTelefono("987654321");
            vet = veterinarioRepository.save(vet);

            // ── 2. Usuarios del sistema ────────────────────────────────

            Usuarios admin = new Usuarios();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRol("ADMIN");
            usuarioRepository.save(admin);

            Usuarios recepcion = new Usuarios();
            recepcion.setUsername("recepcion");
            recepcion.setPassword(passwordEncoder.encode("recepcion123"));
            recepcion.setRol("RECEPCION");
            usuarioRepository.save(recepcion);

            Usuarios usuarioVet = new Usuarios();
            usuarioVet.setUsername("carlos");
            usuarioVet.setPassword(passwordEncoder.encode("vet123"));
            usuarioVet.setRol("VETERINARIO");
            usuarioVet.setVeterinario(vet);
            usuarioRepository.save(usuarioVet);

            // ── 3. Cliente predeterminado ──────────────────────────────
            Cliente cliente = new Cliente();
            cliente.setDni("12345678");
            cliente.setNombre("María");
            cliente.setApellido("García");
            cliente.setTelefono("987123456");
            cliente.setEmail("maria.garcia@email.com");
            cliente.setDireccion("Av. Los Jardines 456, Lima");
            cliente.setFechRegistro(LocalDate.now().minusMonths(2));
            cliente = clienteRepository.save(cliente);

            // ── 4. Mascotas ────────────────────────────────────────────
            Mascota luna = new Mascota();
            luna.setNombre("Luna");
            luna.setEspecie("Perro");
            luna.setRaza("Golden Retriever");
            luna.setSexo("hembra");
            luna.setEdad(3);
            luna.setPeso(25.0);
            luna.setCliente(cliente);
            luna = mascotaRepository.save(luna);

            Mascota michi = new Mascota();
            michi.setNombre("Michi");
            michi.setEspecie("Gato");
            michi.setRaza("Siamés");
            michi.setSexo("macho");
            michi.setEdad(2);
            michi.setPeso(4.2);
            michi.setCliente(cliente);
            michi = mascotaRepository.save(michi);

            // ── 5. Citas + Historial Clínico ───────────────────────────

            // Cita 1: COMPLETADA — Luna, hace 3 semanas
            Cita cita1 = new Cita();
            cita1.setFechaCita(LocalDate.now().minusWeeks(3));
            cita1.setHoraCita(LocalTime.of(9, 0));
            cita1.setMotivo("Control general y vacunación");
            cita1.setMascota(luna);
            cita1.setVeterinario(vet);
            cita1.setEstado("COMPLETADA");
            cita1 = citaRepository.save(cita1);

            HistorialClinico h1 = new HistorialClinico();
            h1.setFechaConsulta(cita1.getFechaCita());
            h1.setDiagnostico("Paciente en buen estado general. Vacunación al día.");
            h1.setTratamiento("Vacuna polivalente aplicada. Desparasitación interna con fenbendazol.");
            h1.setObservaciones("Peso adecuado para su edad. Próxima vacuna en 12 meses.");
            h1.setMascota(luna);
            h1.setCita(cita1);
            historialRepository.save(h1);

            // Cita 2: COMPLETADA — Michi, hace 1 semana
            Cita cita2 = new Cita();
            cita2.setFechaCita(LocalDate.now().minusWeeks(1));
            cita2.setHoraCita(LocalTime.of(10, 30));
            cita2.setMotivo("Revisión por pérdida de apetito");
            cita2.setMascota(michi);
            cita2.setVeterinario(vet);
            cita2.setEstado("COMPLETADA");
            cita2 = citaRepository.save(cita2);

            HistorialClinico h2 = new HistorialClinico();
            h2.setFechaConsulta(cita2.getFechaCita());
            h2.setDiagnostico("Gastritis leve. Sin signos de infección.");
            h2.setTratamiento("Omeprazol 2.5mg cada 24h por 7 días. Dieta blanda los primeros 3 días.");
            h2.setObservaciones("Reevaluar en 10 días si no mejora el apetito.");
            h2.setMascota(michi);
            h2.setCita(cita2);
            historialRepository.save(h2);

            // Cita 3: Programada — Luna, mañana
            Cita cita3 = new Cita();
            cita3.setFechaCita(LocalDate.now().plusDays(1));
            cita3.setHoraCita(LocalTime.of(11, 0));
            cita3.setMotivo("Control post-vacuna y revisión de piel");
            cita3.setMascota(luna);
            cita3.setVeterinario(vet);
            cita3.setEstado("Programada");
            citaRepository.save(cita3);
        };
    }
}
