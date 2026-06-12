package com.itscadmo.centro_fitness.Data.Service;

import java.util.List;
import com.itscadmo.centro_fitness.DTO.ClienteDTO;

public interface ClienteService {

    // CRUD base
    ClienteDTO findById(Long id);
    ClienteDTO findByUsername(String username);
    List<ClienteDTO> findAll();
    ClienteDTO update(Long id, ClienteDTO clienteDTO);
    void delete(Long id);

    // Business logic
    boolean verificaAbbonamentiValidi(Long clienteId);
}