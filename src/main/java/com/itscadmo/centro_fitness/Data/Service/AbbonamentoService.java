package com.itscadmo.centro_fitness.Data.Service;

import com.itscadmo.centro_fitness.DTO.AbbonamentoDTO;
import java.util.List;

public interface AbbonamentoService {

    // Creazione e gestione
    AbbonamentoDTO creaAbbonamento(AbbonamentoDTO abbonamentoDTO);
    AbbonamentoDTO rinnovaAbbonamento(Long abbonamentoId);
    void cancellaAbbonamento(Long id);

    // Lettura
    AbbonamentoDTO findById(Long id);
    List<AbbonamentoDTO> findAll();
    List<AbbonamentoDTO> findByClienteId(Long clienteId);
    List<AbbonamentoDTO> findAttiviByClienteId(Long clienteId);

    // Eliminazione fisica (solo admin)
    void delete(Long id);

    // Manutenzione automatica
    void controlloScadenzaAbbonamenti();
}