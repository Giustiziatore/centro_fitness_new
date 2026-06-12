package com.itscadmo.centro_fitness.Controller;

import com.itscadmo.centro_fitness.DTO.AbbonamentoDTO;
import com.itscadmo.centro_fitness.Data.Service.AbbonamentoService;
import com.itscadmo.centro_fitness.Data.Service.ClienteService;
import com.itscadmo.centro_fitness.DTO.ClienteDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/abbonamenti")
@RequiredArgsConstructor
public class AbbonamentoController {

    private final AbbonamentoService abbonamentoService;
    private final ClienteService clienteService;

    // --- 1. SOTTOSCRIVI ABBONAMENTO ---
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    public ResponseEntity<AbbonamentoDTO> sottoscriviAbbonamento(
            @Valid @RequestBody AbbonamentoDTO abbonamentoDTO,
            Authentication authentication) {

        // Se è un cliente, può creare abbonamento solo per se stesso
        if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CLIENTE"))) {
            ClienteDTO clienteAutenticato = clienteService.findByUsername(authentication.getName());
            if (!clienteAutenticato.getId().equals(abbonamentoDTO.getClienteId())) {
                throw new SecurityException("Non autorizzato a creare abbonamenti per altri clienti");
            }
        }

        AbbonamentoDTO nuovoAbbonamento = abbonamentoService.creaAbbonamento(abbonamentoDTO);
        return new ResponseEntity<>(nuovoAbbonamento, HttpStatus.CREATED);
    }

    // --- 2. VISUALIZZA TUTTI GLI ABBONAMENTI (Solo Admin) ---
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AbbonamentoDTO>> getAllAbbonamenti() {
        List<AbbonamentoDTO> abbonamenti = abbonamentoService.findAll();
        return ResponseEntity.ok(abbonamenti);
    }

    // --- 3. VISUALIZZA SINGOLO ABBONAMENTO ---
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ISTRUTTORE', 'CLIENTE')")
    public ResponseEntity<AbbonamentoDTO> getAbbonamentoById(@PathVariable Long id) {
        AbbonamentoDTO abbonamento = abbonamentoService.findById(id);
        return ResponseEntity.ok(abbonamento);
    }

    // --- 4. VISUALIZZA ABBONAMENTI DI UN CLIENTE ---
    // Caso d'uso: Cliente vede i propri abbonamenti, Admin/Istruttore vedono quelli di qualsiasi cliente
    @GetMapping("/cliente/{clienteId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ISTRUTTORE', 'CLIENTE')")
    public ResponseEntity<List<AbbonamentoDTO>> getAbbonamentiCliente(
            @PathVariable Long clienteId,
            Authentication authentication) {

        // Se è un cliente, può vedere solo i propri
        if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CLIENTE"))) {
            ClienteDTO clienteAutenticato = clienteService.findByUsername(authentication.getName());
            if (!clienteAutenticato.getId().equals(clienteId)) {
                throw new SecurityException("Non autorizzato a visualizzare abbonamenti di altri clienti");
            }
        }

        List<AbbonamentoDTO> abbonamenti = abbonamentoService.findByClienteId(clienteId);
        return ResponseEntity.ok(abbonamenti);
    }

    // --- 5. VISUALIZZA ABBONAMENTI ATTIVI DI UN CLIENTE ---
    @GetMapping("/cliente/{clienteId}/attivi")
    @PreAuthorize("hasAnyRole('ADMIN', 'ISTRUTTORE', 'CLIENTE')")
    public ResponseEntity<List<AbbonamentoDTO>> getAbbonamentiAttivi(
            @PathVariable Long clienteId,
            Authentication authentication) {

        // Controllo sicurezza come sopra
        if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CLIENTE"))) {
            ClienteDTO clienteAutenticato = clienteService.findByUsername(authentication.getName());
            if (!clienteAutenticato.getId().equals(clienteId)) {
                throw new SecurityException("Non autorizzato");
            }
        }

        List<AbbonamentoDTO> abbonamenti = abbonamentoService.findAttiviByClienteId(clienteId);
        return ResponseEntity.ok(abbonamenti);
    }

    // --- 6. RINNOVA ABBONAMENTO ---
    @PostMapping("/{id}/rinnova")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    public ResponseEntity<AbbonamentoDTO> rinnovaAbbonamento(
            @PathVariable Long id,
            Authentication authentication) {

        // Verifica proprietà se è un cliente
        if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CLIENTE"))) {
            AbbonamentoDTO abbonamento = abbonamentoService.findById(id);
            ClienteDTO clienteAutenticato = clienteService.findByUsername(authentication.getName());
            if (!clienteAutenticato.getId().equals(abbonamento.getClienteId())) {
                throw new SecurityException("Non autorizzato a rinnovare questo abbonamento");
            }
        }

        AbbonamentoDTO rinnovato = abbonamentoService.rinnovaAbbonamento(id);
        return new ResponseEntity<>(rinnovato, HttpStatus.CREATED);
    }

    // --- 7. CANCELLA ABBONAMENTO (Soft delete) ---
    @PutMapping("/{id}/cancella")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    public ResponseEntity<Void> cancellaAbbonamento(
            @PathVariable Long id,
            Authentication authentication) {

        // Verifica proprietà se è un cliente
        if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CLIENTE"))) {
            AbbonamentoDTO abbonamento = abbonamentoService.findById(id);
            ClienteDTO clienteAutenticato = clienteService.findByUsername(authentication.getName());
            if (!clienteAutenticato.getId().equals(abbonamento.getClienteId())) {
                throw new SecurityException("Non autorizzato a cancellare questo abbonamento");
            }
        }

        abbonamentoService.cancellaAbbonamento(id);
        return ResponseEntity.noContent().build();
    }

    // --- 8. ELIMINA ABBONAMENTO (Hard delete - Solo Admin) ---
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminaAbbonamento(@PathVariable Long id) {
        abbonamentoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}