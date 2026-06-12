package com.itscadmo.centro_fitness.Data.Service;

import com.itscadmo.centro_fitness.Data.DAO.SalaDAO;
import com.itscadmo.centro_fitness.DTO.SalaDTO;
import com.itscadmo.centro_fitness.Data.Entity.Sala;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SalaServiceImpl implements SalaService {

    private final SalaDAO salaDAO;
    private final ModelMapper modelMapper;

    private SalaDTO toDTO(Sala sala) {
        return modelMapper.map(sala, SalaDTO.class);
    }

    private Sala toEntity(SalaDTO dto) {
        return modelMapper.map(dto, Sala.class);
    }

    @Override
    @Transactional
    public SalaDTO create(SalaDTO salaDTO) {
        log.info("Creazione nuova sala: {}", salaDTO.getNome());

        // Verifica unicità nome
        if (salaDAO.existsByNome(salaDTO.getNome())) {
            throw new IllegalArgumentException(
                    "Esiste già una sala con nome: " + salaDTO.getNome());
        }

        Sala sala = toEntity(salaDTO);
        Sala saved = salaDAO.save(sala);
        log.info("Sala creata con ID: {}", saved.getIdSala());

        return toDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public SalaDTO findById(Long id) {
        log.info("Ricerca sala con ID: {}", id);
        Sala sala = salaDAO.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Sala non trovata con id: %s", id)));
        return toDTO(sala);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SalaDTO> findAll() {
        log.info("Ricerca di tutte le sale");
        return salaDAO.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SalaDTO update(Long id, SalaDTO salaDTO) {
        log.info("Aggiornamento sala con ID: {}", id);

        Sala existingSala = salaDAO.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Sala non trovata con id: %s", id)));

        // Verifica unicità nome se è stato cambiato
        if (!existingSala.getNome().equals(salaDTO.getNome())
                && salaDAO.existsByNome(salaDTO.getNome())) {
            throw new IllegalArgumentException(
                    "Esiste già una sala con nome: " + salaDTO.getNome());
        }

        existingSala.setNome(salaDTO.getNome());
        existingSala.setCapienza(salaDTO.getCapienza());
        existingSala.setOrariApertura(salaDTO.getOrariApertura());

        Sala updated = salaDAO.save(existingSala);
        log.info("Sala aggiornata con successo: ID {}", id);

        return toDTO(updated);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Eliminazione sala con ID: {}", id);

        if (!salaDAO.existsById(id)) {
            throw new EntityNotFoundException(
                    String.format("Sala non trovata con id: %s", id));
        }


        salaDAO.deleteById(id);
        log.warn("Sala eliminata con successo: ID {}", id);
    }
}