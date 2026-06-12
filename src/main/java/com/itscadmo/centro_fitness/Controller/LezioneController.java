package com.itscadmo.centro_fitness.Controller;

import com.itscadmo.centro_fitness.DTO.LezioneDTO;
import com.itscadmo.centro_fitness.Data.Entity.StatoLezione;
import com.itscadmo.centro_fitness.Data.Service.LezioneService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/lezioni")
@RequiredArgsConstructor
public class LezioneController {

    private final LezioneService lezioneService;

    // --- 1. VISUALIZZA TUTTE LE LEZIONI (Pubblico) ---
    @GetMapping
    public ResponseEntity<List<LezioneDTO>> getAllLezioni() {
        List<LezioneDTO> lezioni = lezioneService.findAll();
        return ResponseEntity.ok(lezioni);
    }

    // --- 2. VISUALIZZA SINGOLA LEZIONE (Pubblico) ---
    @GetMapping("/{id}")
    public ResponseEntity<LezioneDTO> getLezioneById(@PathVariable Long id) {
        LezioneDTO lezione = lezioneService.findById(id);
        return ResponseEntity.ok(lezione);
    }

    // --- 3. LEZIONI PER CORSO (Pubblico) ---
    @GetMapping("/corso/{corsoId}")
    public ResponseEntity<List<LezioneDTO>> getLezioniPerCorso(@PathVariable Long corsoId) {
        List<LezioneDTO> lezioni = lezioneService.findByCorsoId(corsoId);
        return ResponseEntity.ok(lezioni);
    }

    // --- 4. LEZIONI PER SALA (Pubblico) ---
    @GetMapping("/sala/{salaId}")
    public ResponseEntity<List<LezioneDTO>> getLezioniPerSala(@PathVariable Long salaId) {
        List<LezioneDTO> lezioni = lezioneService.findBySalaId(salaId);
        return ResponseEntity.ok(lezioni);
    }

    // --- 5. LEZIONI PER ISTRUTTORE (Pubblico) ---
    @GetMapping("/istruttore/{istruttoreId}")
    public ResponseEntity<List<LezioneDTO>> getLezioniPerIstruttore(@PathVariable Long istruttoreId) {
        List<LezioneDTO> lezioni = lezioneService.findByIstruttoreId(istruttoreId);
        return ResponseEntity.ok(lezioni);
    }

    // --- 6. LEZIONI FUTURE (Pubblico) ---
    @GetMapping("/future")
    public ResponseEntity<List<LezioneDTO>> getLezioniFuture() {
        List<LezioneDTO> lezioni = lezioneService.findLezioniFuture();
        return ResponseEntity.ok(lezioni);
    }

    // --- 7. LEZIONI IN RANGE DATE (Pubblico) ---
    @GetMapping("/range")
    public ResponseEntity<List<LezioneDTO>> getLezioniInRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<LezioneDTO> lezioni = lezioneService.findLezioniInRange(start, end);
        return ResponseEntity.ok(lezioni);
    }

    // --- 8. CREA LEZIONE (Solo Admin) ---
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LezioneDTO> creaLezione(@Valid @RequestBody LezioneDTO lezioneDTO) {
        LezioneDTO nuovaLezione = lezioneService.create(lezioneDTO);
        return new ResponseEntity<>(nuovaLezione, HttpStatus.CREATED);
    }

    // --- 9. AGGIORNA LEZIONE (Solo Admin) ---
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LezioneDTO> aggiornaLezione(
            @PathVariable Long id,
            @Valid @RequestBody LezioneDTO lezioneDTO) {
        LezioneDTO aggiornata = lezioneService.update(id, lezioneDTO);
        return ResponseEntity.ok(aggiornata);
    }

    // --- 10. CANCELLA LEZIONE (Solo Admin) ---
    @PutMapping("/{id}/cancella")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LezioneDTO> cancellaLezione(@PathVariable Long id) {
        LezioneDTO cancellata = lezioneService.cancellaLezione(id);
        return ResponseEntity.ok(cancellata);
    }

    // --- 11. COMPLETA LEZIONE (Admin o Istruttore) ---
    @PutMapping("/{id}/completa")
    @PreAuthorize("hasAnyRole('ADMIN', 'ISTRUTTORE')")
    public ResponseEntity<LezioneDTO> completaLezione(@PathVariable Long id) {
        LezioneDTO completata = lezioneService.completaLezione(id);
        return ResponseEntity.ok(completata);
    }

    // --- 12. ELIMINA LEZIONE (Solo Admin) ---
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminaLezione(@PathVariable Long id) {
        lezioneService.delete(id);
        return ResponseEntity.noContent().build();
    }
}