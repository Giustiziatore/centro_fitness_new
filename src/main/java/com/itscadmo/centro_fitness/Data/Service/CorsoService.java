package com.itscadmo.centro_fitness.Data.Service;

import com.itscadmo.centro_fitness.DTO.CorsoDTO;
import java.util.List;

public interface CorsoService {

    CorsoDTO create(CorsoDTO corsoDTO);
    CorsoDTO findById(Long id);
    List<CorsoDTO> findAll();
    CorsoDTO update(Long id, CorsoDTO corsoDTO);
    void delete(Long id);

    // Query personalizzate
    List<CorsoDTO> findBySalaId(Long salaId);
    List<CorsoDTO> findByIstruttoreId(Long istruttoreId);
    List<CorsoDTO> findByGiornoSettimana(String giornoSettimana);
    List<CorsoDTO> searchByNome(String nome);
}