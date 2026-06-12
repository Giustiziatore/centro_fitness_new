package com.itscadmo.centro_fitness.Controller;

import com.itscadmo.centro_fitness.DTO.IscrizioneLezioneDTO;
import com.itscadmo.centro_fitness.Data.Service.ClienteService;
import com.itscadmo.centro_fitness.Data.Service.IscrizioneLezioneService;
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
@RequestMapping("/api/iscrizioni")
@RequiredArgsConstructor
public class IscrizioneLezioneController {

    private final IscrizioneLezioneService iscrizioneLezioneService;
    private final ClienteService clienteService;

    // --- 1. VISUALIZZA TUTTE LE ISCRIZIONI (Solo Admin/Istruttore) ---
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ISTRUTTORE')")
    public ResponseEntity<List<IscrizioneLezioneDTO>> getAllIscrizioni() {
        List<IscrizioneLezioneDTO> iscrizioni = iscrizioneLezioneService.findAll();
        return ResponseEntity.ok(iscrizioni);
    }

    // --- 2. VISUALIZZA SINGOLA ISCRIZIONE ---
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ISTRUTTORE', 'CLIENTE')")
    public ResponseEntity<IscrizioneLezioneDTO> getIscrizioneById(@PathVariable Long id) {
        IscrizioneLezioneDTO iscrizione = iscrizioneLezioneService.findById(id);
        return ResponseEntity.ok(iscrizione);
    }

    // --- 3. ISCRIZIONI PER LEZIONE (Admin/Istruttore) ---
    @GetMapping("/lezione/{lezioneId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ISTRUTTORE')")
    public ResponseEntity<List<IscrizioneLezioneDTO>> getIscrizioniPerLezione(@PathVariable Long lezioneId) {
        List<IscrizioneLezioneDTO> iscrizioni = iscrizioneLezioneService.findByLezioneId(lezioneId);
        return ResponseEntity.ok(iscrizioni);
    }

    // --- 4. ISCRIZIONI PER CLIENTE ---
    @GetMapping("/cliente/{clienteId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ISTRUTTORE', 'CLIENTE')")
    public ResponseEntity<List<IscrizioneLezioneDTO>> getIscrizioniPerCliente(
            @PathVariable Long clienteId,
            Authentication authentication) {

        // Se è un cliente, può vedere solo le proprie
        if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CLIENTE"))) {
            ClienteDTO clienteAutenticato = clienteService.findByUsername(authentication.getName());
            if (!clienteAutenticato.getId().equals(clienteId)) {
                throw new SecurityException("Non autorizzato");
            }
        }

        List<IscrizioneLezioneDTO> iscrizioni = iscrizioneLezioneService.findByClienteId(clienteId);
        return ResponseEntity.ok(iscrizioni);
    }

    // --- 5. LE MIE ISCRIZIONI (Cliente loggato) ---
    @GetMapping("/me")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<List<IscrizioneLezioneDTO>> getMieIscrizioni(Authentication authentication) {
        String username = authentication.getName();
        ClienteDTO cliente = clienteService.findByUsername(username);
        List<IscrizioneLezioneDTO> iscrizioni = iscrizioneLezioneService.findByClienteId(cliente.getId());
        return ResponseEntity.ok(iscrizioni);
    }

    // --- 6. ISCRIVITI A LEZIONE ---
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    public ResponseEntity<IscrizioneLezioneDTO> iscriviLezione(
            @Valid @RequestBody IscrizioneLezioneDTO iscrizioneDTO,
            Authentication authentication) {

        // Se è un cliente, può iscrivere solo se stesso
        if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CLIENTE"))) {
            ClienteDTO clienteAutenticato = clienteService.findByUsername(authentication.getName());
            if (!clienteAutenticato.getId().equals(iscrizioneDTO.getClienteId())) {
                throw new SecurityException("Non autorizzato a iscrivere altri clienti");
            }
        }

        IscrizioneLezioneDTO nuovaIscrizione = iscrizioneLezioneService.iscriviCliente(iscrizioneDTO);
        return new ResponseEntity<>(nuovaIscrizione, HttpStatus.CREATED);
    }

    // --- 7. CANCELLA ISCRIZIONE ---
    @PutMapping("/{id}/cancella")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    public ResponseEntity<IscrizioneLezioneDTO> cancellaIscrizione(
            @PathVariable Long id,
            Authentication authentication) {

        // Verifica proprietà se è un cliente
        if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CLIENTE"))) {
            IscrizioneLezioneDTO iscrizione = iscrizioneLezioneService.findById(id);
            ClienteDTO clienteAutenticato = clienteService.findByUsername(authentication.getName());
            if (!clienteAutenticato.getId().equals(iscrizione.getClienteId())) {
                throw new SecurityException("Non autorizzato a cancellare questa iscrizione");
            }
        }

        IscrizioneLezioneDTO cancellata = iscrizioneLezioneService.cancellaIscrizione(id);
        return ResponseEntity.ok(cancellata);
    }

    // --- 8. ELIMINA ISCRIZIONE (Solo Admin) ---
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminaIscrizione(@PathVariable Long id) {
        iscrizioneLezioneService.delete(id);
        return ResponseEntity.noContent().build();
    }
}