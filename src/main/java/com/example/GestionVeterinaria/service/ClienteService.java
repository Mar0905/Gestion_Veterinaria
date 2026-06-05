package com.example.GestionVeterinaria.service;

import com.example.GestionVeterinaria.entity.Cliente;
import com.example.GestionVeterinaria.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ClienteService {

    //Inicializamos el repositorio
    private final ClienteRepository clienteRepository;


    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    //listar clientes
    public List<Cliente> listarTodos(){
        return clienteRepository.findAll();
    }

    //Buscar cliente por id
    public Cliente devolverCliente_id(Long id) {
        return clienteRepository.findById(id).
                orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
    }


    public Cliente registrar(Cliente cliente){

        if(clienteRepository.existsByDni(cliente.getDni())){
            throw  new RuntimeException("el dni ya está registrado");
        }

        cliente.setFechRegistro(LocalDate.now());
        return clienteRepository.save(cliente);

    }

    //Eliminar cliente
    public void eliminar(Long id){
        Cliente cliente =devolverCliente_id(id);
        clienteRepository.delete(cliente);
    }


}
