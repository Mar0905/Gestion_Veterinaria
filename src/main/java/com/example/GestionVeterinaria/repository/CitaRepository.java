package com.example.GestionVeterinaria.repository;

import com.example.GestionVeterinaria.entity.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface CitaRepository extends JpaRepository<Cita,Long> {

    List<Cita> findByMascotaId(Long mascota_id);
    List<Cita> findByVeterinario(Long veterinario_id);
    List<Cita> findByFechaCita(LocalDate fechaCita);
    int countByVeterinarioId(Long veterinarioId);
    void deleteById(Long id);
    List<Cita> findByVeterinarioId(Long veterinarioId);
    int countByVeterinarioIdAndFechaCita(Long veterinarioId, LocalDate fechaCita);
    boolean existsByVeterinarioIdAndFechaCitaAndHoraCita(
            Long veterinarioId,
            LocalDate fechaCita,
            LocalTime horaCita,
            String estado
    );
    List<Cita> findByVeterinarioIdAndEstado(Long veterinarioId, String estado);
    List<Cita> findByEstado(String estado);
}
