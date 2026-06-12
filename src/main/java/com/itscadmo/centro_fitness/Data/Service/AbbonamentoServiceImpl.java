package com.itscadmo.centro_fitness.Data.Service;

import com.itscadmo.centro_fitness.Data.DAO.AbbonamentoDAO;
import com.itscadmo.centro_fitness.Data.DAO.ClienteDAO;
import com.itscadmo.centro_fitness.DTO.AbbonamentoDTO;
import com.itscadmo.centro_fitness.Data.Entity.Abbonamento;
import com.itscadmo.centro_fitness.Data.Entity.Cliente;
import com.itscadmo.centro_fitness.Data.Entity.DurataAbbonamento;
import com.itscadmo.centro_fitness.Data.Entity.StatoAbbonamento;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AbbonamentoServiceImpl implements AbbonamentoService {

    private final AbbonamentoDAO abbonamentoDAO;
    private final ClienteDAO clienteDAO;
    private final ModelMapper modelMapper;

    // --- Conversione ---

    private AbbonamentoDTO toDTO(Abbonamento abbonamento) {
        AbbonamentoDTO dto = modelMapper.map(abbonamento, AbbonamentoDTO.class);
        dto.setClienteId(abbonamento.getCliente().getId());
        return dto;
    }

    // --- CRUD ---

    @Override
    @Transactional
    public AbbonamentoDTO creaAbbonamento(AbbonamentoDTO abbonamentoDTO) {
        log.info("Creazione abbonamento per cliente ID: {}", abbonamentoDTO.getClienteId());

        // 1. Valida input
        if (abbonamentoDTO.getClienteId() == null) {
            throw new IllegalArgumentException("Cliente ID obbligatorio");
        }
        if (abbonamentoDTO.getDurata() == null) {
            throw new IllegalArgumentException("Durata abbonamento obbligatoria");
        }

        // 2. Recupera cliente
        Cliente cliente = clienteDAO.findById(abbonamentoDTO.getClienteId())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Cliente non trovato con id: %s", abbonamentoDTO.getClienteId())));

        // 3. Valida tesseramento base
        LocalDate oggi = LocalDate.now();
        if (cliente.getDataScadenzaTessera() == null ||
                cliente.getDataScadenzaTessera().isBefore(oggi)) {
            throw new IllegalStateException(
                    "Impossibile creare abbonamento: tesseramento base scaduto. Rinnovare la tessera prima.");
        }

        // 4. Crea abbonamento
        Abbonamento abbonamento = new Abbonamento();
        abbonamento.setCliente(cliente);
        abbonamento.setDurata(abbonamentoDTO.getDurata());
        abbonamento.setDataInizio(oggi);
        abbonamento.setDataFine(calcolaDataFine(oggi, abbonamentoDTO.getDurata()));
        abbonamento.setStato(StatoAbbonamento.ATTIVO);
        abbonamento.setPrezzo(abbonamentoDTO.getDurata().getPrezzo()); // Usa il prezzo dall'enum

        Abbonamento saved = abbonamentoDAO.save(abbonamento);
        log.info("Abbonamento creato con ID: {}", saved.getIdAbbonamento());

        return toDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public AbbonamentoDTO findById(Long id) {
        log.info("Ricerca abbonamento con ID: {}", id);
        Abbonamento abbonamento = abbonamentoDAO.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Abbonamento non trovato con id: %s", id)));
        return toDTO(abbonamento);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AbbonamentoDTO> findAll() {
        log.info("Ricerca di tutti gli abbonamenti");
        return abbonamentoDAO.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AbbonamentoDTO> findByClienteId(Long clienteId) {
        log.info("Ricerca abbonamenti del cliente ID: {}", clienteId);

        // Verifica esistenza cliente
        if (!clienteDAO.existsById(clienteId)) {
            throw new EntityNotFoundException(
                    String.format("Cliente non trovato con id: %s", clienteId));
        }

        return abbonamentoDAO.findByClienteId(clienteId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AbbonamentoDTO> findAttiviByClienteId(Long clienteId) {
        log.info("Ricerca abbonamenti attivi del cliente ID: {}", clienteId);

        LocalDate oggi = LocalDate.now();
        return abbonamentoDAO.findByClienteId(clienteId).stream()
                .filter(a -> a.getStato() == StatoAbbonamento.ATTIVO)
                .filter(a -> a.getDataFine() != null && !a.getDataFine().isBefore(oggi))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AbbonamentoDTO rinnovaAbbonamento(Long abbonamentoId) {
        log.info("Rinnovo abbonamento ID: {}", abbonamentoId);

        Abbonamento vecchioAbbonamento = abbonamentoDAO.findById(abbonamentoId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Abbonamento non trovato con id: %s", abbonamentoId)));

        // Crea nuovo abbonamento con stessa durata
        Abbonamento nuovoAbbonamento = new Abbonamento();
        nuovoAbbonamento.setCliente(vecchioAbbonamento.getCliente());
        nuovoAbbonamento.setDurata(vecchioAbbonamento.getDurata());

        LocalDate oggi = LocalDate.now();
        nuovoAbbonamento.setDataInizio(oggi);
        nuovoAbbonamento.setDataFine(calcolaDataFine(oggi, vecchioAbbonamento.getDurata()));
        nuovoAbbonamento.setStato(StatoAbbonamento.ATTIVO);
        nuovoAbbonamento.setPrezzo(vecchioAbbonamento.getDurata().getPrezzo());

        Abbonamento saved = abbonamentoDAO.save(nuovoAbbonamento);
        log.info("Abbonamento rinnovato con nuovo ID: {}", saved.getIdAbbonamento());

        return toDTO(saved);
    }

    @Override
    @Transactional
    public void cancellaAbbonamento(Long id) {
        log.info("Cancellazione abbonamento ID: {}", id);

        Abbonamento abbonamento = abbonamentoDAO.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Abbonamento non trovato con id: %s", id)));

        // Cambio lo stato invece di eliminare (soft delete)
        abbonamento.setStato(StatoAbbonamento.CANCELLATO);
        abbonamentoDAO.save(abbonamento);

        log.warn("Abbonamento cancellato: ID {}", id);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Eliminazione abbonamento ID: {}", id);

        if (!abbonamentoDAO.existsById(id)) {
            throw new EntityNotFoundException(
                    String.format("Abbonamento non trovato con id: %s", id));
        }

        abbonamentoDAO.deleteById(id);
        log.warn("Abbonamento eliminato: ID {}", id);
    }

    @Override
    @Transactional
    public void controlloScadenzaAbbonamenti() {
        log.info("Esecuzione controllo scadenza abbonamenti");

        LocalDate oggi = LocalDate.now();
        List<Abbonamento> scaduti = abbonamentoDAO
                .findByDataFineBeforeAndStato(oggi, StatoAbbonamento.ATTIVO);

        scaduti.forEach(ab -> {
            ab.setStato(StatoAbbonamento.SCADUTO);
            log.info("Abbonamento ID {} impostato come SCADUTO", ab.getIdAbbonamento());
        });

        abbonamentoDAO.saveAll(scaduti);
        log.info("Aggiornati {} abbonamenti scaduti", scaduti.size());
    }

    // --- Helper ---

    private LocalDate calcolaDataFine(LocalDate inizio, DurataAbbonamento durata) {
        switch (durata) {
            case MENSILE:
                return inizio.plusMonths(1).minusDays(1);
            case TRIMESTRALE:
                return inizio.plusMonths(3).minusDays(1);
            case SEMESTRALE:
                return inizio.plusMonths(6).minusDays(1);
            case ANNUALE:
                return inizio.plusYears(1).minusDays(1);
            default:
                throw new IllegalArgumentException("Durata non gestita: " + durata);
        }
    }
}