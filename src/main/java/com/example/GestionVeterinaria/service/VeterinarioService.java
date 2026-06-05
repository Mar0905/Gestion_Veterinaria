package com.example.GestionVeterinaria.service;

import com.example.GestionVeterinaria.entity.Veterinario;
import com.example.GestionVeterinaria.repository.CitaRepository;
import com.example.GestionVeterinaria.repository.VeterinarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class VeterinarioService {

    private final VeterinarioRepository veterinarioRepository;
    private  final CitaRepository citaRepository;

    public VeterinarioService(VeterinarioRepository veterinarioRepository, CitaRepository citaRepository) {
        this.veterinarioRepository = veterinarioRepository;

        this.citaRepository = citaRepository;
    }
    public  void eliminarVeterinario(Long id){
        veterinarioRepository.deleteById(id);
    }
    public String disponibilidadHoy(Long veterinarioId) {

        long cantidad = citaRepository
                .countByVeterinarioIdAndFechaCita(
                        veterinarioId,
                        LocalDate.now()
                );

        return cantidad >= 3 ? "Ocupado" : "Disponible";
    }

    public List<Veterinario> listarTodos(){
        return veterinarioRepository.findAll();
    }

    public Veterinario buscarId(Long id_veterinario){
        return  veterinarioRepository.findById(id_veterinario).
                orElseThrow(()-> new RuntimeException("Veterinario no encontrado"));
    }

    public Veterinario registraVeterinario(Veterinario veterinario){
        return veterinarioRepository.save(veterinario);
    }

    public void eliminarVeterinario(Veterinario id_veterinario){
        veterinarioRepository.delete(id_veterinario);
    }
    public  int contarCitas(Long veterinarioId) {
        return citaRepository.countByVeterinarioId(veterinarioId);

    }
}
