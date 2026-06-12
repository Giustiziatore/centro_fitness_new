package com.itscadmo.centro_fitness.Data.Service;

import com.itscadmo.centro_fitness.DTO.SalaDTO;
import java.util.List;

public interface SalaService {

    SalaDTO create(SalaDTO salaDTO);
    SalaDTO findById(Long id);
    List<SalaDTO> findAll();
    SalaDTO update(Long id, SalaDTO salaDTO);
    void delete(Long id);
}