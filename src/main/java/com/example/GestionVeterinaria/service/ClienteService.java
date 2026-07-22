package com.example.GestionVeterinaria.service;

import com.example.GestionVeterinaria.entity.Cliente;
import com.example.GestionVeterinaria.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

// Servicio con la lógica de negocio para el registro, edición y eliminación de clientes
@Service
public class ClienteService {

    //Inicializamos el repositorio
    private final ClienteRepository clienteRepository;


    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    // Lista todos los clientes registrados
    public List<Cliente> listarTodos(){
        return clienteRepository.findAll();
    }

    // Busca un cliente por id
    public Cliente devolverCliente_id(Long id) {
        return clienteRepository.findById(id).
                orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
    }


    // Registra un nuevo cliente y valida DNI duplicado
    public Cliente registrar(Cliente cliente){

        if(clienteRepository.existsByDni(cliente.getDni())){
            throw  new RuntimeException("el dni ya está registrado");
        }

        cliente.setFechRegistro(LocalDate.now());
        return clienteRepository.save(cliente);

    }

    // Actualiza los datos de un cliente existente
    public Cliente actualizar(Long id, Cliente datos) {
        Cliente existente = devolverCliente_id(id);
        existente.setNombre(datos.getNombre());
        existente.setApellido(datos.getApellido());
        existente.setTelefono(datos.getTelefono());
        existente.setEmail(datos.getEmail());
        existente.setDireccion(datos.getDireccion());
        return clienteRepository.save(existente);
    }

    // Elimina un cliente por id
    public void eliminar(Long id){
        Cliente cliente =devolverCliente_id(id);
        clienteRepository.delete(cliente);
    }


}
