package com.example.GestionVeterinaria.service;

import com.example.GestionVeterinaria.entity.Cliente;
import com.example.GestionVeterinaria.entity.Mascota;
import com.example.GestionVeterinaria.repository.ClienteRepository;
import com.example.GestionVeterinaria.repository.MascotaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MascotaService {

    private final MascotaRepository mascotaRepository;
    private final ClienteRepository clienteRepository;

    public MascotaService(MascotaRepository mascotaRepository,
                          ClienteRepository clienteRepository) {
        this.mascotaRepository = mascotaRepository;
        this.clienteRepository = clienteRepository;
    }

    // Listar todas las mascotas
    public List<Mascota> listarTodas() {
        return mascotaRepository.findAll();
    }

    // Listar mascotas por cliente
    public List<Mascota> listarPorCliente(Long clienteId) {
        if(!clienteRepository.existsById(clienteId)){
            throw  new RuntimeException("Cliente no encontrado");
        }
        return mascotaRepository.findByCliente_id(clienteId);
    }

   public Mascota buscarPorId(Long id){
     return mascotaRepository.findById(id).orElseThrow(()->
             new RuntimeException("Mascota no encontrada"));
   }

   //Registrar mascota
    public Mascota registrar(Mascota mascota, Long id_cliente){
        Cliente cliente= clienteRepository.findById(id_cliente).
                orElseThrow(()-> new RuntimeException("Cliente no encontrado"));
        //Asociamos cliente con mascota
        mascota.setCliente(cliente);
        return mascotaRepository.save(mascota);
    }


    //Eliminar por id
    public void eliminarMascota(Long id){
        Mascota mascota= buscarPorId(id);
        mascotaRepository.delete(mascota);
    }
}
