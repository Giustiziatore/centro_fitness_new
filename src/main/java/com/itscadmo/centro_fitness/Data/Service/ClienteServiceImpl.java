package com.itscadmo.centro_fitness.Data.Service;

import com.itscadmo.centro_fitness.Data.DAO.AbbonamentoDAO;
import com.itscadmo.centro_fitness.Data.DAO.ClienteDAO;
import com.itscadmo.centro_fitness.DTO.ClienteDTO;
import com.itscadmo.centro_fitness.Data.Entity.Abbonamento;
import com.itscadmo.centro_fitness.Data.Entity.Cliente;
import com.itscadmo.centro_fitness.Data.Entity.StatoAbbonamento;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClienteServiceImpl implements ClienteService {

    private final ClienteDAO clienteDAO;
    private final AbbonamentoDAO abbonamentoDAO; 
    private final ModelMapper modelMapper;


    private ClienteDTO toDTO(Cliente cliente) {
        ClienteDTO dto = modelMapper.map(cliente, ClienteDTO.class);

        LocalDate oggi = LocalDate.now();
        
        
        dto.setAttivo(abbonamentoDAO.existsAbbonamentiAttiviByClienteId(cliente.getId(), oggi));

        try {
            cliente.getAbbonamenti().size(); // Force load
            cliente.getAbbonamenti().stream()
                    .filter(a -> a.getStato() == StatoAbbonamento.ATTIVO)
                    .filter(a -> a.getDataFine() != null && !a.getDataFine().isBefore(oggi))
                    .map(Abbonamento::getDataFine)
                    .max(LocalDate::compareTo)
                    .ifPresent(dto::setUltimaScadenzaAbbonamentoServizio);
        } catch (Exception e) {
            log.warn("Impossibile caricare abbonamenti per cliente ID: {}", cliente.getId());
        }

        return dto;
    }

    @Transactional(readOnly = true)
    @Override
    public ClienteDTO findById(Long id) {
        log.info("Ricerca cliente con ID: {}", id);
        Cliente cliente = clienteDAO.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Cliente non trovato con id: %s", id)));
        return toDTO(cliente);
    }

    @Transactional(readOnly = true)
    @Override
    public ClienteDTO findByUsername(String username) {
        log.info("Ricerca cliente con username: {}", username);
        Cliente cliente = clienteDAO.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Cliente non trovato con username: %s", username)));
        return toDTO(cliente);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ClienteDTO> findAll() {
        log.info("Ricerca di tutti i clienti");
        return clienteDAO.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ClienteDTO update(Long id, ClienteDTO clienteDTO) {
        log.info("Aggiornamento cliente con ID: {}", id);

        Cliente existingCliente = clienteDAO.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Cliente non trovato con id: %s", id)));

        existingCliente.setNome(clienteDTO.getNome());
        existingCliente.setCognome(clienteDTO.getCognome());
        existingCliente.setEmail(clienteDTO.getEmail());
        existingCliente.setTelefono(clienteDTO.getTelefono());
        existingCliente.setIndirizzo(clienteDTO.getIndirizzo());
        existingCliente.setObiettivoFitness(clienteDTO.getObiettivoFitness());

        Cliente updatedCliente = clienteDAO.save(existingCliente);
        log.info("Cliente aggiornato con successo: ID {}", id);

        return toDTO(updatedCliente);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        log.info("Eliminazione cliente con ID: {}", id);

        if (!clienteDAO.existsById(id)) {
            throw new EntityNotFoundException(
                    String.format("Cliente non trovato con id: %s", id));
        }

        clienteDAO.deleteById(id);
        log.warn("Cliente eliminato con successo: ID {}", id);
    }

    @Transactional(readOnly = true)
    @Override
    public boolean verificaAbbonamentiValidi(Long clienteId) {
        log.info("Verifica validità abbonamenti per cliente ID: {}", clienteId);
        
        Cliente cliente = clienteDAO.findById(clienteId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Cliente non trovato con id: %s", clienteId)));
        
        LocalDate oggi = LocalDate.now();
        
        // Verifica tessera base
        if (cliente.getDataScadenzaTessera() == null || 
            cliente.getDataScadenzaTessera().isBefore(oggi)) {
            log.warn("Tessera base scaduta per cliente ID: {}", clienteId);
            return false;
        }

        boolean hasAbbonamentiAttivi = abbonamentoDAO
            .existsAbbonamentiAttiviByClienteId(clienteId, oggi);
        
        log.info("Cliente ID {} ha abbonamenti validi: {}", clienteId, hasAbbonamentiAttivi);
        return hasAbbonamentiAttivi;
    }

    private String generaNumeroTessera(String nome, String cognome) {
        String prefix = nome.substring(0, Math.min(1, nome.length())).toUpperCase()
                + cognome.substring(0, Math.min(1, cognome.length())).toUpperCase();
        String uniquePart = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return prefix + "_" + uniquePart;
    }
}