package com.itscadmo.centro_fitness.Data.Service;

import com.itscadmo.centro_fitness.Data.DAO.*;
import com.itscadmo.centro_fitness.DTO.IscrizioneLezioneDTO;
import com.itscadmo.centro_fitness.Data.Entity.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class IscrizioneLezioneServiceImpl implements IscrizioneLezioneService {

    private final IscrizioneLezioneDAO iscrizioneLezioneDAO;
    private final LezioneDAO lezioneDAO;
    private final ClienteDAO clienteDAO;
    private final ClienteService clienteService; 

    private IscrizioneLezioneDTO toDTO(IscrizioneLezione iscrizione) {
        IscrizioneLezioneDTO dto = new IscrizioneLezioneDTO();

        dto.setId(iscrizione.getId());
        dto.setLezioneId(iscrizione.getLezione().getIdLezione());
        dto.setClienteId(iscrizione.getCliente().getId());
        dto.setDataIscrizione(iscrizione.getDataIscrizione());
        dto.setStato(iscrizione.getStato());

        Cliente cliente = iscrizione.getCliente();
        dto.setNomeCliente(cliente.getNome());
        dto.setCognomeCliente(cliente.getCognome());
        
        dto.setAbbonamentoAttivo(clienteService.verificaAbbonamentiValidi(cliente.getId()));

        Lezione lezione = iscrizione.getLezione();
        dto.setDataOraLezione(lezione.getDataOraInizio());
        dto.setNomeSala(lezione.getSala().getNome());

        if (lezione.getCorso() != null) {
            dto.setNomeCorso(lezione.getCorso().getNome());
        }

        return dto;
    }

    @Override
    @Transactional
    public IscrizioneLezioneDTO iscriviCliente(IscrizioneLezioneDTO iscrizioneDTO) {
        log.info("Iscrizione cliente ID {} a lezione ID {}",
                iscrizioneDTO.getClienteId(), iscrizioneDTO.getLezioneId());

        Lezione lezione = lezioneDAO.findById(iscrizioneDTO.getLezioneId())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Lezione non trovata con id: %s", iscrizioneDTO.getLezioneId())));

        Cliente cliente = clienteDAO.findById(iscrizioneDTO.getClienteId())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Cliente non trovato con id: %s", iscrizioneDTO.getClienteId())));

        if (lezione.getStato() != StatoLezione.PROGRAMMATA) {
            throw new IllegalStateException(
                    "La lezione non è più aperta alle iscrizioni (Stato: " + lezione.getStato() + ")");
        }

        if (lezione.getDataOraInizio().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Non è possibile iscriversi a lezioni passate");
        }

        if (!clienteService.verificaAbbonamentiValidi(cliente.getId())) {
            throw new IllegalStateException(
                    String.format("Il cliente %s %s non ha un abbonamento valido",
                            cliente.getNome(), cliente.getCognome()));
        }

        if (iscrizioneLezioneDAO.existsByLezioneIdLezioneAndClienteIdAndStato(
                lezione.getIdLezione(), cliente.getId(), StatoIscrizione.CONFERMATA)) {
            throw new IllegalStateException("Cliente già iscritto a questa lezione");
        }

        long iscrittiConfermati = iscrizioneLezioneDAO.countIscrittiConfermati(lezione.getIdLezione());
        if (iscrittiConfermati >= lezione.getSala().getCapienza()) {
            throw new IllegalStateException(
                    String.format("Lezione al completo (Capienza: %d)", lezione.getSala().getCapienza()));
        }

        IscrizioneLezione iscrizione = new IscrizioneLezione();
        iscrizione.setLezione(lezione);
        iscrizione.setCliente(cliente);
        iscrizione.setDataIscrizione(LocalDateTime.now());
        iscrizione.setStato(StatoIscrizione.CONFERMATA);

        IscrizioneLezione saved = iscrizioneLezioneDAO.save(iscrizione);
        log.info("Iscrizione creata con ID: {}", saved.getId());

        return toDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public IscrizioneLezioneDTO findById(Long id) {
        log.info("Ricerca iscrizione con ID: {}", id);
        IscrizioneLezione iscrizione = iscrizioneLezioneDAO.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Iscrizione non trovata con id: %s", id)));
        return toDTO(iscrizione);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IscrizioneLezioneDTO> findAll() {
        log.info("Ricerca di tutte le iscrizioni");
        return iscrizioneLezioneDAO.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<IscrizioneLezioneDTO> findByLezioneId(Long lezioneId) {
        log.info("Ricerca iscrizioni per lezione ID: {}", lezioneId);

        if (!lezioneDAO.existsById(lezioneId)) {
            throw new EntityNotFoundException(
                    String.format("Lezione non trovata con id: %s", lezioneId));
        }

        return iscrizioneLezioneDAO.findByLezioneIdLezione(lezioneId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<IscrizioneLezioneDTO> findByClienteId(Long clienteId) {
        log.info("Ricerca iscrizioni per cliente ID: {}", clienteId);

        if (!clienteDAO.existsById(clienteId)) {
            throw new EntityNotFoundException(
                    String.format("Cliente non trovato con id: %s", clienteId));
        }

        return iscrizioneLezioneDAO.findByClienteId(clienteId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public IscrizioneLezioneDTO cancellaIscrizione(Long id) {
        log.info("Cancellazione iscrizione ID: {}", id);

        IscrizioneLezione iscrizione = iscrizioneLezioneDAO.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Iscrizione non trovata con id: %s", id)));

        if (iscrizione.getLezione().getDataOraInizio().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Non è possibile cancellare iscrizioni a lezioni passate");
        }

        iscrizione.setStato(StatoIscrizione.CANCELLATA);
        IscrizioneLezione updated = iscrizioneLezioneDAO.save(iscrizione);
        log.info("Iscrizione cancellata: ID {}", id);

        return toDTO(updated);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Eliminazione iscrizione ID: {}", id);

        if (!iscrizioneLezioneDAO.existsById(id)) {
            throw new EntityNotFoundException(
                    String.format("Iscrizione non trovata con id: %s", id));
        }

        iscrizioneLezioneDAO.deleteById(id);
        log.warn("Iscrizione eliminata: ID {}", id);
    }
}