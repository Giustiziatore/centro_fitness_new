package com.itscadmo.centro_fitness.Data.Service;

import com.itscadmo.centro_fitness.Data.DAO.*;
import com.itscadmo.centro_fitness.DTO.LezioneDTO;
import com.itscadmo.centro_fitness.Data.Entity.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LezioneServiceImpl implements LezioneService {

    private final LezioneDAO lezioneDAO;
    private final CorsoDAO corsoDAO;
    private final SalaDAO salaDAO;
    private final IstruttoreDAO istruttoreDAO;
    private final ModelMapper modelMapper;

    // --- Conversione ---

    private LezioneDTO toDTO(Lezione lezione) {
        LezioneDTO dto = new LezioneDTO();

        dto.setIdLezione(lezione.getIdLezione());
        dto.setDataOraInizio(lezione.getDataOraInizio());
        dto.setDataOraFine(lezione.getDataOraFine());
        dto.setStato(lezione.getStato());

        // Sala
        if (lezione.getSala() != null) {
            dto.setSalaId(lezione.getSala().getIdSala());
            dto.setNomeSala(lezione.getSala().getNome());
            dto.setCapienzaSala(lezione.getSala().getCapienza());
        }

        // Corso
        if (lezione.getCorso() != null) {
            dto.setCorsoId(lezione.getCorso().getIdCorso());
            dto.setNomeCorso(lezione.getCorso().getNome());
        }

        // Istruttori
        if (lezione.getIstruttori() != null && !lezione.getIstruttori().isEmpty()) {
            dto.setIstruttoriIds(lezione.getIstruttori().stream()
                    .map(Istruttore::getId)
                    .collect(Collectors.toList()));

            dto.setNomiIstruttori(lezione.getIstruttori().stream()
                    .map(i -> i.getNome() + " " + i.getCognome())
                    .collect(Collectors.toList()));
        }

        // Info iscrizioni
        dto.setNumeroIscritti(lezione.getNumeroPartecipanti());
        dto.setPiena(lezione.isPiena());

        return dto;
    }

    // --- CRUD ---

    @Override
    @Transactional
    public LezioneDTO create(LezioneDTO lezioneDTO) {
        log.info("Creazione nuova lezione per corso ID: {}", lezioneDTO.getCorsoId());

        // 1. Valida corso
        Corso corso = corsoDAO.findById(lezioneDTO.getCorsoId())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Corso non trovato con id: %s", lezioneDTO.getCorsoId())));

        // 2. Valida sala
        Sala sala = salaDAO.findById(lezioneDTO.getSalaId())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Sala non trovata con id: %s", lezioneDTO.getSalaId())));

        // 3. Valida istruttori
        List<Istruttore> istruttori = istruttoreDAO.findAllById(lezioneDTO.getIstruttoriIds());
        if (istruttori.size() != lezioneDTO.getIstruttoriIds().size()) {
            throw new EntityNotFoundException("Uno o più istruttori non trovati");
        }

        // 4. Valida date
        if (lezioneDTO.getDataOraFine().isBefore(lezioneDTO.getDataOraInizio())) {
            throw new IllegalArgumentException("La data fine deve essere successiva alla data inizio");
        }

        if (lezioneDTO.getDataOraInizio().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Non è possibile programmare lezioni nel passato");
        }

        // 5. Verifica conflitti sala
        List<Lezione> conflittiSala = lezioneDAO.findConflittiSala(
                lezioneDTO.getSalaId(),
                lezioneDTO.getDataOraInizio(),
                lezioneDTO.getDataOraFine()
        );

        if (!conflittiSala.isEmpty()) {
            throw new IllegalStateException(
                    String.format("La sala '%s' è già occupata in questo orario", sala.getNome()));
        }

        // 6. Verifica conflitti istruttori
        for (Long istruttoreId : lezioneDTO.getIstruttoriIds()) {
            List<Lezione> conflittiIstruttore = lezioneDAO.findConflittiIstruttore(
                    istruttoreId,
                    lezioneDTO.getDataOraInizio(),
                    lezioneDTO.getDataOraFine()
            );

            if (!conflittiIstruttore.isEmpty()) {
                Istruttore istr = istruttori.stream()
                        .filter(i -> i.getId().equals(istruttoreId))
                        .findFirst()
                        .orElse(null);

                throw new IllegalStateException(
                        String.format("L'istruttore %s %s ha già una lezione in questo orario",
                                istr != null ? istr.getNome() : "",
                                istr != null ? istr.getCognome() : ""));
            }
        }

        // 7. Crea lezione
        Lezione lezione = new Lezione();
        lezione.setDataOraInizio(lezioneDTO.getDataOraInizio());
        lezione.setDataOraFine(lezioneDTO.getDataOraFine());
        lezione.setStato(StatoLezione.PROGRAMMATA);
        lezione.setSala(sala);
        lezione.setCorso(corso);
        lezione.setIstruttori(istruttori);

        Lezione saved = lezioneDAO.save(lezione);
        log.info("Lezione creata con ID: {}", saved.getIdLezione());

        return toDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public LezioneDTO findById(Long id) {
        log.info("Ricerca lezione con ID: {}", id);
        Lezione lezione = lezioneDAO.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Lezione non trovata con id: %s", id)));
        return toDTO(lezione);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LezioneDTO> findAll() {
        log.info("Ricerca di tutte le lezioni");
        return lezioneDAO.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LezioneDTO> findByCorsoId(Long corsoId) {
        log.info("Ricerca lezioni per corso ID: {}", corsoId);

        if (!corsoDAO.existsById(corsoId)) {
            throw new EntityNotFoundException(
                    String.format("Corso non trovato con id: %s", corsoId));
        }

        return lezioneDAO.findByCorsoIdCorso(corsoId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LezioneDTO> findBySalaId(Long salaId) {
        log.info("Ricerca lezioni per sala ID: {}", salaId);

        if (!salaDAO.existsById(salaId)) {
            throw new EntityNotFoundException(
                    String.format("Sala non trovata con id: %s", salaId));
        }

        return lezioneDAO.findBySalaIdSala(salaId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LezioneDTO> findByIstruttoreId(Long istruttoreId) {
        log.info("Ricerca lezioni per istruttore ID: {}", istruttoreId);

        if (!istruttoreDAO.existsById(istruttoreId)) {
            throw new EntityNotFoundException(
                    String.format("Istruttore non trovato con id: %s", istruttoreId));
        }

        return lezioneDAO.findByIstruttoreId(istruttoreId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LezioneDTO> findLezioniFuture() {
        log.info("Ricerca lezioni future");
        return lezioneDAO.findByDataOraInizioAfterAndStato(
                        LocalDateTime.now(), StatoLezione.PROGRAMMATA)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LezioneDTO> findLezioniInRange(LocalDateTime start, LocalDateTime end) {
        log.info("Ricerca lezioni tra {} e {}", start, end);
        return lezioneDAO.findByDataOraInizioBetween(start, end).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public LezioneDTO update(Long id, LezioneDTO lezioneDTO) {
        log.info("Aggiornamento lezione con ID: {}", id);

        Lezione existingLezione = lezioneDAO.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Lezione non trovata con id: %s", id)));

        // Non permettere modifica di lezioni già completate o cancellate
        if (existingLezione.getStato() == StatoLezione.COMPLETATA ||
                existingLezione.getStato() == StatoLezione.CANCELLATA) {
            throw new IllegalStateException(
                    "Non è possibile modificare lezioni completate o cancellate");
        }

        // Aggiorna i campi (simile a "create" con validazioni)
        existingLezione.setDataOraInizio(lezioneDTO.getDataOraInizio());
        existingLezione.setDataOraFine(lezioneDTO.getDataOraFine());

        Lezione updated = lezioneDAO.save(existingLezione);
        log.info("Lezione aggiornata con successo: ID {}", id);

        return toDTO(updated);
    }

    @Override
    @Transactional
    public LezioneDTO cambiaStato(Long id, StatoLezione nuovoStato) {
        log.info("Cambio stato lezione ID {} a {}", id, nuovoStato);

        Lezione lezione = lezioneDAO.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Lezione non trovata con id: %s", id)));

        lezione.setStato(nuovoStato);
        Lezione updated = lezioneDAO.save(lezione);

        return toDTO(updated);
    }

    @Override
    @Transactional
    public LezioneDTO cancellaLezione(Long id) {
        return cambiaStato(id, StatoLezione.CANCELLATA);
    }

    @Override
    @Transactional
    public LezioneDTO completaLezione(Long id) {
        return cambiaStato(id, StatoLezione.COMPLETATA);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Eliminazione lezione con ID: {}", id);

        if (!lezioneDAO.existsById(id)) {
            throw new EntityNotFoundException(
                    String.format("Lezione non trovata con id: %s", id));
        }

        lezioneDAO.deleteById(id);
        log.warn("Lezione eliminata con successo: ID {}", id);
    }
}