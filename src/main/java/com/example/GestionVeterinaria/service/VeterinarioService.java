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
    // Elimina un veterinario por id
    public  void eliminarVeterinario(Long id){
        veterinarioRepository.deleteById(id);
    }
    // Indica si un veterinario está disponible u ocupado según sus citas de hoy
    public String disponibilidadHoy(Long veterinarioId) {

        long cantidad = citaRepository
                .countByVeterinarioIdAndFechaCita(
                        veterinarioId,
                        LocalDate.now()
                );

        return cantidad >= 3 ? "Ocupado" : "Disponible";
    }

    // Lista todos los veterinarios registrados
    public List<Veterinario> listarTodos(){
        return veterinarioRepository.findAll();
    }

    // Busca un veterinario por id
    public Veterinario buscarId(Long id_veterinario){
        return  veterinarioRepository.findById(id_veterinario).
                orElseThrow(()-> new RuntimeException("Veterinario no encontrado"));
    }

    // Registra un nuevo veterinario
    public Veterinario registraVeterinario(Veterinario veterinario){
        return veterinarioRepository.save(veterinario);
    }

    // Actualiza los datos de un veterinario existente
    public Veterinario actualizar(Long id, Veterinario datos) {
        Veterinario existente = buscarId(id);
        existente.setNombre(datos.getNombre());
        existente.setEspecialidad(datos.getEspecialidad());
        existente.setTelefono(datos.getTelefono());
        return veterinarioRepository.save(existente);
    }

    // Elimina un veterinario dado
    public void eliminarVeterinario(Veterinario id_veterinario){
        veterinarioRepository.delete(id_veterinario);
    }
    // Cuenta el total de citas asignadas a un veterinario
    public  int contarCitas(Long veterinarioId) {
        return citaRepository.countByVeterinarioId(veterinarioId);

    }
}
