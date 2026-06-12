package com.itscadmo.centro_fitness.Controller;

import java.util.List;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.itscadmo.centro_fitness.DTO.ClienteDTO;
import com.itscadmo.centro_fitness.Data.Service.ClienteService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/clienti")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    // --- 1. LETTURA TUTTI I CLIENTI (Solo Admin e Istruttori) ---
    // Caso d'uso: Admin visualizza tutti i clienti, Istruttore vede i suoi clienti assegnati
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ISTRUTTORE')")
    public ResponseEntity<List<ClienteDTO>> getAllClienti() {
        List<ClienteDTO> clienti = clienteService.findAll();
        return ResponseEntity.ok(clienti);
    }

    // --- 2. LETTURA SINGOLO CLIENTE ---
    // Caso d'uso:
    // - Admin/Istruttore: possono vedere qualsiasi cliente
    // - Cliente: può vedere solo il proprio profilo
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ISTRUTTORE', 'CLIENTE')")
    public ResponseEntity<ClienteDTO> getClienteById(
            @PathVariable Long id,
            Authentication authentication) {

        ClienteDTO cliente = clienteService.findById(id);

        // Se è un cliente, può vedere solo se stesso
        if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CLIENTE"))) {
            // Verifica che stia accedendo al proprio profilo
            ClienteDTO clienteAutenticato = clienteService.findByUsername(authentication.getName());
            if (!clienteAutenticato.getId().equals(id)) {
                throw new SecurityException("Non autorizzato a visualizzare questo profilo");
            }
        }

        return ResponseEntity.ok(cliente);
    }

    // --- 3. AGGIORNAMENTO PROFILO CLIENTE ---
    // Caso d'uso:
    // - Admin: può modificare qualsiasi cliente
    // - Cliente: può modificare solo il proprio profilo (dati anagrafici, obiettivo fitness)
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    public ResponseEntity<ClienteDTO> aggiornaCliente(
            @PathVariable Long id,
            @Valid @RequestBody ClienteDTO clienteDTO,
            Authentication authentication) {

        // Se è un cliente, può modificare solo se stesso
        if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CLIENTE"))) {
            ClienteDTO clienteAutenticato = clienteService.findByUsername(authentication.getName());
            if (!clienteAutenticato.getId().equals(id)) {
                throw new SecurityException("Non autorizzato a modificare questo profilo");
            }
        }

        ClienteDTO clienteAggiornato = clienteService.update(id, clienteDTO);
        return ResponseEntity.ok(clienteAggiornato);
    }

    // --- 4. ELIMINAZIONE CLIENTE (Solo Admin) ---
    // Caso d'uso: Admin elimina un cliente (rare, meglio disattivare)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminaCliente(@PathVariable Long id) {
        clienteService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // --- 5. VISUALIZZA PROFILO PERSONALE (Shortcut per il cliente loggato) ---
    // Caso d'uso: Cliente loggato vuole vedere il proprio profilo senza conoscere l'ID
    @GetMapping("/me")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ClienteDTO> getProfilioPersonale(Authentication authentication) {
        String username = authentication.getName();
        ClienteDTO cliente = clienteService.findByUsername(username);
        return ResponseEntity.ok(cliente);
    }

    // --- 6. VERIFICA ABBONAMENTO ATTIVO (Utile per il frontend) ---
    // Caso d'uso: Cliente controlla se può accedere ai servizi
    @GetMapping("/{id}/abbonamento-attivo")
    @PreAuthorize("hasAnyRole('ADMIN', 'ISTRUTTORE', 'CLIENTE')")
    public ResponseEntity<Boolean> verificaAbbonamentoAttivo(
            @PathVariable Long id,
            Authentication authentication) {

        // Controllo sicurezza (come sopra)
        if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CLIENTE"))) {
            ClienteDTO clienteAutenticato = clienteService.findByUsername(authentication.getName());
            if (!clienteAutenticato.getId().equals(id)) {
                throw new SecurityException("Non autorizzato");
            }
        }

        boolean attivo = clienteService.verificaAbbonamentiValidi(id);
        return ResponseEntity.ok(attivo);
    }
}