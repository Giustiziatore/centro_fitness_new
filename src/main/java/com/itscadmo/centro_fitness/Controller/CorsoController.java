package com.itscadmo.centro_fitness.Controller;

import com.itscadmo.centro_fitness.DTO.CorsoDTO;
import com.itscadmo.centro_fitness.Data.Service.CorsoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/corsi")
@RequiredArgsConstructor
public class CorsoController {

    private final CorsoService corsoService;

    // --- 1. VISUALIZZA TUTTI I CORSI (Pubblico) ---
    // Caso d'uso: Chiunque può vedere i corsi disponibili
    @GetMapping
    public ResponseEntity<List<CorsoDTO>> getAllCorsi() {
        List<CorsoDTO> corsi = corsoService.findAll();
        return ResponseEntity.ok(corsi);
    }

    // --- 2. VISUALIZZA SINGOLO CORSO (Pubblico) ---
    @GetMapping("/{id}")
    public ResponseEntity<CorsoDTO> getCorsoById(@PathVariable Long id) {
        CorsoDTO corso = corsoService.findById(id);
        return ResponseEntity.ok(corso);
    }

    // --- 3. CORSI PER SALA (Pubblico) ---
    // Caso d'uso: Vedere tutti i corsi che si tengono in una sala specifica
    @GetMapping("/sala/{salaId}")
    public ResponseEntity<List<CorsoDTO>> getCorsiPerSala(@PathVariable Long salaId) {
        List<CorsoDTO> corsi = corsoService.findBySalaId(salaId);
        return ResponseEntity.ok(corsi);
    }

    // --- 4. CORSI PER ISTRUTTORE (Pubblico) ---
    // Caso d'uso: Vedere tutti i corsi tenuti da un istruttore
    @GetMapping("/istruttore/{istruttoreId}")
    public ResponseEntity<List<CorsoDTO>> getCorsiPerIstruttore(@PathVariable Long istruttoreId) {
        List<CorsoDTO> corsi = corsoService.findByIstruttoreId(istruttoreId);
        return ResponseEntity.ok(corsi);
    }

    // --- 5. CORSI PER GIORNO SETTIMANA (Pubblico) ---
    // Caso d'uso: Vedere tutti i corsi di un giorno specifico
    @GetMapping("/giorno/{giornoSettimana}")
    public ResponseEntity<List<CorsoDTO>> getCorsiPerGiorno(@PathVariable String giornoSettimana) {
        List<CorsoDTO> corsi = corsoService.findByGiornoSettimana(giornoSettimana);
        return ResponseEntity.ok(corsi);
    }

    // --- 6. RICERCA CORSI PER NOME (Pubblico) ---
    // Caso d'uso: Cercare corsi per nome (es: "Yoga", "Spinning")
    @GetMapping("/search")
    public ResponseEntity<List<CorsoDTO>> searchCorsi(@RequestParam String nome) {
        List<CorsoDTO> corsi = corsoService.searchByNome(nome);
        return ResponseEntity.ok(corsi);
    }

    // --- 7. CREA CORSO (Solo Admin) ---
    // Caso d'uso: Admin crea un nuovo corso
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CorsoDTO> creaCorso(@Valid @RequestBody CorsoDTO corsoDTO) {
        CorsoDTO nuovoCorso = corsoService.create(corsoDTO);
        return new ResponseEntity<>(nuovoCorso, HttpStatus.CREATED);
    }

    // --- 8. AGGIORNA CORSO (Solo Admin) ---
    // Caso d'uso: Admin modifica un corso esistente
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CorsoDTO> aggiornaCorso(
            @PathVariable Long id,
            @Valid @RequestBody CorsoDTO corsoDTO) {
        CorsoDTO aggiornato = corsoService.update(id, corsoDTO);
        return ResponseEntity.ok(aggiornato);
    }

    // --- 9. ELIMINA CORSO (Solo Admin) ---
    // Caso d'uso: Admin elimina un corso
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminaCorso(@PathVariable Long id) {
        corsoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}