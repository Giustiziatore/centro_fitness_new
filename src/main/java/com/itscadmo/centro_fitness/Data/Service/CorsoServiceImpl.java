package com.itscadmo.centro_fitness.Data.Service;

import com.itscadmo.centro_fitness.Data.DAO.CorsoDAO;
import com.itscadmo.centro_fitness.Data.DAO.IstruttoreDAO;
import com.itscadmo.centro_fitness.Data.DAO.SalaDAO;
import com.itscadmo.centro_fitness.DTO.CorsoDTO;
import com.itscadmo.centro_fitness.Data.Entity.Corso;
import com.itscadmo.centro_fitness.Data.Entity.Istruttore;
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
public class CorsoServiceImpl implements CorsoService {

    private final CorsoDAO corsoDAO;
    private final SalaDAO salaDAO;
    private final IstruttoreDAO istruttoreDAO;
    private final ModelMapper modelMapper;

    // --- Conversione ---

    private CorsoDTO toDTO(Corso corso) {
        CorsoDTO dto = modelMapper.map(corso, CorsoDTO.class);

        // Aggiungi informazioni sala e istruttore
        if (corso.getSala() != null) {
            dto.setSalaId(corso.getSala().getIdSala());
            dto.setNomeSala(corso.getSala().getNome());
        }

        if (corso.getIstruttoreCorso() != null) {
            dto.setIstruttoreId(corso.getIstruttoreCorso().getId());
            dto.setNomeIstruttore(corso.getIstruttoreCorso().getNome());
            dto.setCognomeIstruttore(corso.getIstruttoreCorso().getCognome());
        }

        return dto;
    }

    // --- CRUD ---

    @Override
    @Transactional
    public CorsoDTO create(CorsoDTO corsoDTO) {
        log.info("Creazione nuovo corso: {}", corsoDTO.getNome());

        // 1. Valida sala
        Sala sala = salaDAO.findById(corsoDTO.getSalaId())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Sala non trovata con id: %s", corsoDTO.getSalaId())));

        // 2. Valida istruttore
        Istruttore istruttore = istruttoreDAO.findById(corsoDTO.getIstruttoreId())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Istruttore non trovato con id: %s", corsoDTO.getIstruttoreId())));

        // 3. Verifica unicità (stesso nome, sala, giorno)
        if (corsoDAO.existsByNomeAndSalaIdSalaAndGiornoSettimana(
                corsoDTO.getNome(),
                corsoDTO.getSalaId(),
                corsoDTO.getGiornoSettimana())) {
            throw new IllegalArgumentException(
                    String.format("Esiste già un corso '%s' nella sala '%s' il %s",
                            corsoDTO.getNome(),
                            sala.getNome(),
                            corsoDTO.getGiornoSettimana()));
        }

        // 4. Crea corso
        Corso corso = new Corso();
        corso.setNome(corsoDTO.getNome());
        corso.setDescrizione(corsoDTO.getDescrizione());
        corso.setGiornoSettimana(corsoDTO.getGiornoSettimana().toUpperCase());
        corso.setDurataMinuti(corsoDTO.getDurataMinuti());
        corso.setSala(sala);
        corso.setIstruttoreCorso(istruttore);

        Corso saved = corsoDAO.save(corso);
        log.info("Corso creato con ID: {}", saved.getIdCorso());

        return toDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CorsoDTO findById(Long id) {
        log.info("Ricerca corso con ID: {}", id);
        Corso corso = corsoDAO.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Corso non trovato con id: %s", id)));
        return toDTO(corso);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CorsoDTO> findAll() {
        log.info("Ricerca di tutti i corsi");
        return corsoDAO.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CorsoDTO> findBySalaId(Long salaId) {
        log.info("Ricerca corsi per sala ID: {}", salaId);

        // Verifica esistenza sala
        if (!salaDAO.existsById(salaId)) {
            throw new EntityNotFoundException(
                    String.format("Sala non trovata con id: %s", salaId));
        }

        return corsoDAO.findBySalaIdSala(salaId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CorsoDTO> findByIstruttoreId(Long istruttoreId) {
        log.info("Ricerca corsi per istruttore ID: {}", istruttoreId);

        // Verifica esistenza istruttore
        if (!istruttoreDAO.existsById(istruttoreId)) {
            throw new EntityNotFoundException(
                    String.format("Istruttore non trovato con id: %s", istruttoreId));
        }

        return corsoDAO.findByIstruttoreCorsoId(istruttoreId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CorsoDTO> findByGiornoSettimana(String giornoSettimana) {
        log.info("Ricerca corsi per giorno: {}", giornoSettimana);

        return corsoDAO.findByGiornoSettimana(giornoSettimana.toUpperCase()).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CorsoDTO> searchByNome(String nome) {
        log.info("Ricerca corsi per nome contenente: {}", nome);

        return corsoDAO.findByNomeContaining(nome).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CorsoDTO update(Long id, CorsoDTO corsoDTO) {
        log.info("Aggiornamento corso con ID: {}", id);

        // 1. Carica corso esistente
        Corso existingCorso = corsoDAO.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Corso non trovato con id: %s", id)));

        // 2. Valida sala se cambiata
        if (!existingCorso.getSala().getIdSala().equals(corsoDTO.getSalaId())) {
            Sala nuovaSala = salaDAO.findById(corsoDTO.getSalaId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            String.format("Sala non trovata con id: %s", corsoDTO.getSalaId())));
            existingCorso.setSala(nuovaSala);
        }

        // 3. Valida istruttore se cambiato
        if (!existingCorso.getIstruttoreCorso().getId().equals(corsoDTO.getIstruttoreId())) {
            Istruttore nuovoIstruttore = istruttoreDAO.findById(corsoDTO.getIstruttoreId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            String.format("Istruttore non trovato con id: %s", corsoDTO.getIstruttoreId())));
            existingCorso.setIstruttoreCorso(nuovoIstruttore);
        }

        // 4. Aggiorna campi
        existingCorso.setNome(corsoDTO.getNome());
        existingCorso.setDescrizione(corsoDTO.getDescrizione());
        existingCorso.setGiornoSettimana(corsoDTO.getGiornoSettimana().toUpperCase());
        existingCorso.setDurataMinuti(corsoDTO.getDurataMinuti());

        Corso updated = corsoDAO.save(existingCorso);
        log.info("Corso aggiornato con successo: ID {}", id);

        return toDTO(updated);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Eliminazione corso con ID: {}", id);

        if (!corsoDAO.existsById(id)) {
            throw new EntityNotFoundException(
                    String.format("Corso non trovato con id: %s", id));
        }


        corsoDAO.deleteById(id);
        log.warn("Corso eliminato con successo: ID {}", id);
    }
}