package com.itscadmo.centro_fitness.Data.Service;

import com.itscadmo.centro_fitness.DTO.IstruttoreDTO;
import java.util.List;

public interface IstruttoreService {

    IstruttoreDTO update(Long id, IstruttoreDTO istruttoreDTO);
    // Metodi di sola lettura
    IstruttoreDTO findById(Long id);
    List<IstruttoreDTO> findAll();
    void delete(Long id);
}