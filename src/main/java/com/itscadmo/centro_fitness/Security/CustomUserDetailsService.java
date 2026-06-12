package com.itscadmo.centro_fitness.Security;

import com.itscadmo.centro_fitness.Data.DAO.ClienteDAO;
import com.itscadmo.centro_fitness.Data.DAO.IstruttoreDAO;
import com.itscadmo.centro_fitness.Data.Entity.Utente;
import com.itscadmo.centro_fitness.Data.Entity.Utente.Ruolo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final ClienteDAO clienteDAO;
    private final IstruttoreDAO istruttoreDAO;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // Ricerco nei Clienti, se non c'è, cerco negli Istruttori
        Optional<? extends Utente> utenteOpt = clienteDAO.findByUsername(username);
                if (utenteOpt.isEmpty()) {
                    utenteOpt = istruttoreDAO.findByUsername(username);
                }


        // Estrai l'utente o lancia UsernameNotFoundException
        Utente utente = utenteOpt.orElseThrow(() ->
                new UsernameNotFoundException("Utente non trovato con username: " + username));

        return creaUserDetails(utente);
    }

    /**
     * Converte la tua entità Utente nell'oggetto UserDetails standard di Spring Security.
     */
    private UserDetails creaUserDetails(Utente utente) {

        String ruolo = utente.getRuolo() != null ?
                "ROLE_" + utente.getRuolo().name() :
                "ROLE_" + Ruolo.CLIENTE.name(); // Fallback a cliente se il ruolo è nullo

        // 2. Costruisco l'oggetto UserDetails
        return User.builder()
                .username(utente.getUsername())
                .password(utente.getPassword())
                .authorities(ruolo)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}