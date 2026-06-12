package com.itscadmo.centro_fitness.Data.Service;

import com.itscadmo.centro_fitness.DTO.IscrizioneLezioneDTO;
import java.util.List;

public interface IscrizioneLezioneService {

    IscrizioneLezioneDTO iscriviCliente(IscrizioneLezioneDTO iscrizioneDTO);
    IscrizioneLezioneDTO findById(Long id);
    List<IscrizioneLezioneDTO> findAll();
    List<IscrizioneLezioneDTO> findByLezioneId(Long lezioneId);
    List<IscrizioneLezioneDTO> findByClienteId(Long clienteId);

    IscrizioneLezioneDTO cancellaIscrizione(Long id);
    void delete(Long id);
}