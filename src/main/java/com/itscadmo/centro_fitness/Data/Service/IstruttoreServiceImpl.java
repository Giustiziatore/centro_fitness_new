package com.itscadmo.centro_fitness.Data.Service;

import com.itscadmo.centro_fitness.DTO.IstruttoreDTO;
import com.itscadmo.centro_fitness.Data.DAO.IstruttoreDAO;
import com.itscadmo.centro_fitness.Data.Entity.Istruttore;
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
public class IstruttoreServiceImpl implements IstruttoreService {

    private final IstruttoreDAO istruttoreDAO;
    private final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    public IstruttoreDTO findById(Long id) {
        log.info("Ricerca istruttore con ID: {}", id);
        Istruttore istruttore = istruttoreDAO.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Istruttore non trovato con id: %s", id)));
        return modelMapper.map(istruttore, IstruttoreDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IstruttoreDTO> findAll() {
        log.info("Ricerca di tutti gli istruttori");
        return istruttoreDAO.findAll().stream()
                .map(istruttore -> modelMapper.map(istruttore, IstruttoreDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public IstruttoreDTO update(Long id, IstruttoreDTO istruttoreDTO) {
        log.info("Aggiornamento istruttore con ID: {}", id);

        Istruttore existingIstruttore = istruttoreDAO.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Istruttore non trovato con id: %s", id)));

        modelMapper.map(istruttoreDTO, existingIstruttore);

        Istruttore updatedIstruttore = istruttoreDAO.save(existingIstruttore);
        log.info("Istruttore aggiornato con successo: ID {}", id);

        return modelMapper.map(updatedIstruttore, IstruttoreDTO.class);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Eliminazione istruttore con ID: {}", id);

        if (!istruttoreDAO.existsById(id)) {
            throw new EntityNotFoundException(
                    String.format("Istruttore non trovato con id: %s", id));
        }

        istruttoreDAO.deleteById(id);
        log.warn("Istruttore eliminato con successo: ID {}", id);
    }
}