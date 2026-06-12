package com.itscadmo.centro_fitness.Data.Service;

import com.itscadmo.centro_fitness.DTO.LezioneDTO;
import com.itscadmo.centro_fitness.Data.Entity.StatoLezione;

import java.time.LocalDateTime;
import java.util.List;

public interface LezioneService {

    LezioneDTO create(LezioneDTO lezioneDTO);
    LezioneDTO findById(Long id);
    List<LezioneDTO> findAll();
    LezioneDTO update(Long id, LezioneDTO lezioneDTO);
    void delete(Long id);

    // Query personalizzate
    List<LezioneDTO> findByCorsoId(Long corsoId);
    List<LezioneDTO> findBySalaId(Long salaId);
    List<LezioneDTO> findByIstruttoreId(Long istruttoreId);
    List<LezioneDTO> findLezioniFuture();
    List<LezioneDTO> findLezioniInRange(LocalDateTime start, LocalDateTime end);

    // Gestione stato
    LezioneDTO cambiaStato(Long id, StatoLezione nuovoStato);
    LezioneDTO cancellaLezione(Long id);
    LezioneDTO completaLezione(Long id);
}