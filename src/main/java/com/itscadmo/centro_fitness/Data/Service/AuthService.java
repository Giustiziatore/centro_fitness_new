package com.itscadmo.centro_fitness.Data.Service;

import com.itscadmo.centro_fitness.Data.DAO.RefreshTokenDAO;
import com.itscadmo.centro_fitness.Data.DAO.ClienteDAO;
import com.itscadmo.centro_fitness.Data.DAO.IstruttoreDAO;
import com.itscadmo.centro_fitness.Data.Entity.RefreshToken;
import com.itscadmo.centro_fitness.Data.Entity.Cliente;
import com.itscadmo.centro_fitness.Data.Entity.Istruttore;
import com.itscadmo.centro_fitness.Data.Entity.Utente;
import com.itscadmo.centro_fitness.Data.Entity.Utente.Ruolo;
import com.itscadmo.centro_fitness.DTO.Auth.RispostaAutenticazione;
import com.itscadmo.centro_fitness.DTO.Auth.RichiestaAccesso;
import com.itscadmo.centro_fitness.DTO.Auth.RichiestaRegistrazione;
import com.itscadmo.centro_fitness.Security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    // DIPENDENZE INIETTATE
    private final ClienteDAO clienteDAO;
    private final IstruttoreDAO istruttoreDAO;
    private final RefreshTokenDAO refreshTokenDao;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    @Value("${jwt.refresh-expiration}")
    private Long refreshTokenExpiration;

    // --- METODO DI REGISTRAZIONE PUBBLICA (CLIENTE) ---

    @Transactional
    public RispostaAutenticazione registra(RichiestaRegistrazione request) {

        // 1. VERIFICA UNICITÀ (Username ed Email)
        validaCredenziali(request.getUsername(), request.getEmail());

        // 2. CREAZIONE E MAPPATURA CLIENTE
        Cliente nuovoCliente = new Cliente();

        // Mappatura dati anagrafici e di sicurezza
        nuovoCliente.setUsername(request.getUsername());
        nuovoCliente.setEmail(request.getEmail());
        nuovoCliente.setPassword(passwordEncoder.encode(request.getPassword())); // HASHING
        nuovoCliente.setRuolo(Ruolo.CLIENTE); // Forza il ruolo CLIENTE
        nuovoCliente.setNome(request.getNome());
        nuovoCliente.setCognome(request.getCognome());
        nuovoCliente.setDataNascita(request.getDataNascita());
        nuovoCliente.setTelefono(request.getTelefono());
        nuovoCliente.setIndirizzo(request.getIndirizzo());
        nuovoCliente.setGenere(request.getGenere());

        // 3. LOGICA DI BUSINESS CLIENTE (Tesseramento e Tessera Unica)
        LocalDate oggi = LocalDate.now();
        nuovoCliente.setDataTesseramento(oggi);
        nuovoCliente.setDataScadenzaTessera(oggi.plusYears(1));

        String numeroTessera = generaNumeroTessera(nuovoCliente.getNome(), nuovoCliente.getCognome());
        nuovoCliente.setNumeroTessera(numeroTessera); // Assegna il valore NOT NULL

        // 4. Salvataggio
        Cliente utenteSalvato = clienteDAO.save(nuovoCliente);

        // 5. Generazione Tokens
        UserDetails userDetails = creaMinimalUserDetails(utenteSalvato);
        String accessToken = jwtUtil.generateToken(userDetails);
        String refreshToken = creaRefreshToken(utenteSalvato);

        return RispostaAutenticazione.builder()
                .tokenAccesso(accessToken)
                .tokenRefresh(refreshToken)
                .tipoToken("Bearer")
                .scadeIn(jwtExpiration)
                .nomeUtente(utenteSalvato.getUsername())
                .email(utenteSalvato.getEmail())
                .build();
    }

    private void validaCredenziali(String username, String email) {
        if (clienteDAO.existsByUsername(username) || istruttoreDAO.existsByUsername(username)) {
            throw new RuntimeException("Nome utente già esistente.");
        }
        if (clienteDAO.existsByEmail(email) || istruttoreDAO.existsByEmail(email)) {
            throw new RuntimeException("Email già esistente.");
        }
    }

    // --- METODO DI REGISTRAZIONE PERSONALE (ISTRUTTORE/ADMIN) ---

    @Transactional
    public RispostaAutenticazione registraPersonale(RichiestaRegistrazione request, Ruolo ruolo) {
        validaCredenziali(request.getUsername(), request.getEmail());

        Istruttore nuovoIstruttore = new Istruttore();

        // Mappatura di TUTTI i campi Utente obbligatori
        nuovoIstruttore.setUsername(request.getUsername());
        nuovoIstruttore.setEmail(request.getEmail());
        nuovoIstruttore.setPassword(passwordEncoder.encode(request.getPassword()));
        nuovoIstruttore.setNome(request.getNome());
        nuovoIstruttore.setCognome(request.getCognome());
        nuovoIstruttore.setDataNascita(request.getDataNascita());
        nuovoIstruttore.setGenere(request.getGenere());
        nuovoIstruttore.setTelefono(request.getTelefono());
        nuovoIstruttore.setIndirizzo(request.getIndirizzo());
        nuovoIstruttore.setRuolo(ruolo);

        if (request.getSpecializzazione() != null){
            nuovoIstruttore.setSpecializzazione(request.getSpecializzazione());
        }

        if (request.getOrariDisponibilita() != null){
            nuovoIstruttore.setOrariDisponibilita(request.getOrariDisponibilita());
        }

        // 4. Salvataggio
        Istruttore utenteSalvato = istruttoreDAO.save(nuovoIstruttore);

        // 5. Generazione Tokens
        UserDetails userDetails = creaMinimalUserDetails(utenteSalvato);
        String accessToken = jwtUtil.generateToken(userDetails);
        String refreshToken = creaRefreshToken(utenteSalvato);

        log.info("Nuovo {} registrato: {}", ruolo, utenteSalvato.getUsername());

        return RispostaAutenticazione.builder()
                .tokenAccesso(accessToken)
                .tokenRefresh(refreshToken)
                .tipoToken("Bearer")
                .scadeIn(jwtExpiration)
                .nomeUtente(utenteSalvato.getUsername())
                .email(utenteSalvato.getEmail())
                .build();
    }


    // --- METODO DI LOGIN ---

    @Transactional
    public RispostaAutenticazione login(RichiestaAccesso request) {
        // Autentica l'utente (usa CustomUserDetailsService e PasswordEncoder)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // Recupera l'Entità Utente completa
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Utente utente = trovaUtentePerUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Utente loggato non trovato nel DB."));

        // Genera tokens
        String accessToken = jwtUtil.generateToken(userDetails);
        String refreshToken = creaRefreshToken(utente);

        return RispostaAutenticazione.builder()
                .tokenAccesso(accessToken)
                .tokenRefresh(refreshToken)
                .tipoToken("Bearer")
                .scadeIn(jwtExpiration)
                .nomeUtente(utente.getUsername())
                .email(utente.getEmail())
                .build();
    }

    // --- METODI DI GESTIONE TOKEN ---

    @Transactional
    public RispostaAutenticazione rinnovaToken(String tokenRefreshValue) {

        RefreshToken refreshToken = refreshTokenDao.findByToken(tokenRefreshValue)
                .orElseThrow(() -> new RuntimeException("Token di refresh non valido"));

        if (refreshToken.isExpired()) {
            refreshTokenDao.delete(refreshToken);
            throw new RuntimeException("Token di refresh scaduto");
        }

        Utente utente = refreshToken.getUtente();
        UserDetails userDetails = creaMinimalUserDetails(utente);

        String newAccessToken = jwtUtil.generateToken(userDetails);

        return RispostaAutenticazione.builder()
                .tokenAccesso(newAccessToken)
                .tokenRefresh(tokenRefreshValue)
                .tipoToken("Bearer")
                .scadeIn(jwtExpiration)
                .nomeUtente(utente.getUsername())
                .email(utente.getEmail())
                .build();
    }

    @Transactional
    public void logout(String refreshTokenValue) {
        refreshTokenDao.deleteByToken(refreshTokenValue);
    }

    // --- METODI PRIVATI DI UTILITY ---

    private String generaNumeroTessera(String nome, String cognome) {
        String prefix = nome.substring(0, 1).toUpperCase() + cognome.substring(0, 1).toUpperCase();
        String uniquePart = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return prefix + "_" + uniquePart;
    }

    private String creaRefreshToken(Utente utente) {
        refreshTokenDao.deleteByUtente(utente);

        String token = UUID.randomUUID().toString();
        Instant expiryDate = Instant.now().plusMillis(refreshTokenExpiration);

        RefreshToken refreshToken = RefreshToken.builder()
                .token(token)
                .utente(utente)
                .expiryDate(expiryDate)
                .build();

        refreshTokenDao.save(refreshToken);
        return token;
    }

    private Optional<? extends Utente> trovaUtentePerUsername(String username) {
        Optional<? extends Utente> utenteOpt = clienteDAO.findByUsername(username);

        if (utenteOpt.isEmpty()) {
            utenteOpt = istruttoreDAO.findByUsername(username);
        }
        return utenteOpt;
    }

    private UserDetails creaMinimalUserDetails(Utente utente) {
        String ruolo = utente.getRuolo() != null ? "ROLE_" + utente.getRuolo().name() : "ROLE_CLIENTE";

        return User.builder()
                .username(utente.getUsername())
                .password(utente.getPassword())
                .authorities(ruolo)
                .build();
    }
    // --- CAMBIO PASSWORD ---

    @Transactional
    public void cambiaPassword(String username, String vecchiaPassword, String nuovaPassword) {
        // 1. Trova l'utente
        Utente utente = trovaUtentePerUsername(username)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        // 2. Verifica la vecchia password
        if (!passwordEncoder.matches(vecchiaPassword, utente.getPassword())) {
            throw new RuntimeException("Password attuale non corretta");
        }

        // 3. Valida la nuova password (puoi aggiungere requisiti)
        if (nuovaPassword == null || nuovaPassword.length() < 8) {
            throw new RuntimeException("La nuova password deve contenere almeno 8 caratteri");
        }

        // 4. Aggiorna la password
        utente.setPassword(passwordEncoder.encode(nuovaPassword));

        // 5. Salva in base al tipo di utente
        if (utente instanceof Cliente) {
            clienteDAO.save((Cliente) utente);
        } else if (utente instanceof Istruttore) {
            istruttoreDAO.save((Istruttore) utente);
        }

        // 6. Invalida tutti i refresh token per sicurezza
        refreshTokenDao.deleteByUtente(utente);

        log.info("Password cambiata con successo per l'utente: {}", username);
    }

// --- RESET PASSWORD (per ADMIN) ---

    @Transactional
    public void resetPassword(Long utenteId, String nuovaPassword) {
        // Cerca prima tra i clienti
        Optional<? extends Utente> utenteOpt = clienteDAO.findById(utenteId)
                .map(c -> (Utente) c);

        // Se non trovato, cerca tra gli istruttori
        if (utenteOpt.isEmpty()) {
            utenteOpt = istruttoreDAO.findById(utenteId)
                    .map(i -> (Utente) i);
        }

        Utente utente = utenteOpt
                .orElseThrow(() -> new RuntimeException("Utente non trovato con id: " + utenteId));

        // Aggiorna la password
        utente.setPassword(passwordEncoder.encode(nuovaPassword));

        // Salva in base al tipo
        if (utente instanceof Cliente) {
            clienteDAO.save((Cliente) utente);
        } else if (utente instanceof Istruttore) {
            istruttoreDAO.save((Istruttore) utente);
        }

        // Invalida tutti i token
        refreshTokenDao.deleteByUtente(utente);

        log.warn("Password resettata dall'amministratore per l'utente: {}", utente.getUsername());
    }
}