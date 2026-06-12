package com.itscadmo.centro_fitness.Controller;

import com.itscadmo.centro_fitness.DTO.SalaDTO;
import com.itscadmo.centro_fitness.Data.Service.SalaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sale")
@RequiredArgsConstructor
public class SalaController {

    private final SalaService salaService;

    // --- 1. VISUALIZZA TUTTE LE SALE (Pubblico) ---
    @GetMapping
    public ResponseEntity<List<SalaDTO>> getAllSale() {
        List<SalaDTO> sale = salaService.findAll();
        return ResponseEntity.ok(sale);
    }

    // --- 2. VISUALIZZA SINGOLA SALA (Pubblico) ---
    @GetMapping("/{id}")
    public ResponseEntity<SalaDTO> getSalaById(@PathVariable Long id) {
        SalaDTO sala = salaService.findById(id);
        return ResponseEntity.ok(sala);
    }

    // --- 3. CREA SALA (Solo Admin) ---
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SalaDTO> creaSala(@Valid @RequestBody SalaDTO salaDTO) {
        SalaDTO nuovaSala = salaService.create(salaDTO);
        return new ResponseEntity<>(nuovaSala, HttpStatus.CREATED);
    }

    // --- 4. AGGIORNA SALA (Solo Admin) ---
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SalaDTO> aggiornaSala(
            @PathVariable Long id,
            @Valid @RequestBody SalaDTO salaDTO) {
        SalaDTO aggiornata = salaService.update(id, salaDTO);
        return ResponseEntity.ok(aggiornata);
    }

    // --- 5. ELIMINA SALA (Solo Admin) ---
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminaSala(@PathVariable Long id) {
        salaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}