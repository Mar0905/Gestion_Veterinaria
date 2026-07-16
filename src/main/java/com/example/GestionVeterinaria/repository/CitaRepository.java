package com.example.GestionVeterinaria.repository;

import com.example.GestionVeterinaria.entity.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface CitaRepository extends JpaRepository<Cita,Long> {

    // Busca citas por id de mascota
    List<Cita> findByMascotaId(Long mascota_id);
    // Busca citas por id de veterinario
    List<Cita> findByVeterinario(Long veterinario_id);
    // Busca citas por fecha
    List<Cita> findByFechaCita(LocalDate fechaCita);
    // Cuenta las citas de un veterinario
    int countByVeterinarioId(Long veterinarioId);
    // Elimina una cita por id
    void deleteById(Long id);
    // Busca citas por id de veterinario
    List<Cita> findByVeterinarioId(Long veterinarioId);
    // Cuenta las citas de un veterinario en una fecha determinada
    int countByVeterinarioIdAndFechaCita(Long veterinarioId, LocalDate fechaCita);
    // Verifica si un veterinario ya tiene una cita en esa fecha, hora y estado
    boolean existsByVeterinarioIdAndFechaCitaAndHoraCita(
            Long veterinarioId,
            LocalDate fechaCita,
            LocalTime horaCita,
            String estado
    );
    // Busca las citas de un veterinario con un estado determinado
    List<Cita> findByVeterinarioIdAndEstado(Long veterinarioId, String estado);
    // Busca citas por estado
    List<Cita> findByEstado(String estado);
}
