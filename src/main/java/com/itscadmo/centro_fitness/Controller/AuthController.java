package com.itscadmo.centro_fitness.Controller;

import com.itscadmo.centro_fitness.DTO.Auth.*;
import com.itscadmo.centro_fitness.Data.Entity.Utente;
import com.itscadmo.centro_fitness.Data.Service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth") // Endpoint base per le operazioni di sicurezza
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // --- Pubblico ---
    // --- 1. REGISTRAZIONE (POST) ---
    // URL: POST /api/auth/registra
    @PostMapping("/registra")
    public ResponseEntity<RispostaAutenticazione> registraUtente(@Valid @RequestBody RichiestaRegistrazione request) {
        RispostaAutenticazione risposta = authService.registra(request);
        return new ResponseEntity<>(risposta, HttpStatus.CREATED);
    }

    // --- 2. LOGIN (ACCESSO)  ---
    // URL: POST /api/auth/accedi
    @PostMapping("/accedi")
    public ResponseEntity<RispostaAutenticazione> accedi(
            @Valid @RequestBody RichiestaAccesso request) {
        RispostaAutenticazione risposta = authService.login(request);
        return ResponseEntity.ok(risposta);
    }

    // --- Privato:SOLO ADMIN ---
    @PostMapping("/registra-istruttore")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RispostaAutenticazione> registraIstruttore(
            @Valid @RequestBody RichiestaRegistrazione request) {
        RispostaAutenticazione risposta = authService.registraPersonale(request, Utente.Ruolo.ISTRUTTORE);
        return new ResponseEntity<>(risposta, HttpStatus.CREATED);
    }
    // --- 3. RINNOVO TOKEN (POST) ---
    // URL: POST /api/auth/rinnova
    @PostMapping("/rinnova")
    public ResponseEntity<RispostaAutenticazione> rinnovaToken(@Valid @RequestBody RichiestaRefreshToken request) {

        // Il service gestisce la logica di scadenza/invalidità del token.
        RispostaAutenticazione risposta = authService.rinnovaToken(request.getTokenRefresh());

        // 200 OK
        return ResponseEntity.ok(risposta);
    }

    // --- 4. LOGOUT (POST) ---
    // URL: POST /api/auth/logout
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@Valid @RequestBody RichiestaRefreshToken request) {

        // Il service elimina il token dal DB.
        authService.logout(request.getTokenRefresh());

        // 200 OK con un messaggio di conferma
        Map<String, String> risposta = Map.of("messaggio", "Logout effettuato con successo. Token di refresh eliminato.");
        return ResponseEntity.ok(risposta);
    }
    // --- 5. CAMBIO PASSWORD (POST) ---
// URL: POST /api/auth/cambia-password
    @PostMapping("/cambia-password")
    public ResponseEntity<Map<String, String>> cambiaPassword(
            @Valid @RequestBody RichiestaCambioPassword request,
            Authentication authentication) {

        String username = authentication.getName();
        authService.cambiaPassword(username, request.getVecchiaPassword(), request.getNuovaPassword());

        Map<String, String> risposta = Map.of(
                "messaggio", "Password cambiata con successo. Effettua nuovamente il login."
        );
        return ResponseEntity.ok(risposta);
    }

    // --- 6. RESET PASSWORD (POST) - SOLO ADMIN ---
// URL: POST /api/auth/reset-password
    @PostMapping("/reset-password")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> resetPassword(
            @Valid @RequestBody RichiestaResetPassword request) {

        authService.resetPassword(request.getUtenteId(), request.getNuovaPassword());

        Map<String, String> risposta = Map.of(
                "messaggio", "Password resettata con successo."
        );
        return ResponseEntity.ok(risposta);
    }
}