package com.itscadmo.centro_fitness.Config;

import com.itscadmo.centro_fitness.Data.DAO.IstruttoreDAO;
import com.itscadmo.centro_fitness.Data.Entity.Istruttore;
import com.itscadmo.centro_fitness.Data.Entity.Utente.Ruolo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseAdmin implements CommandLineRunner {

    private final IstruttoreDAO istruttoreDAO;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.bootstrap.enabled:false}")
    private boolean bootstrapAdminEnabled;

    @Value("${app.admin.username:admin}")
    private String adminUsername;

    @Value("${app.admin.password:}")
    private String adminPassword;

    @Value("${app.admin.email:admin@fitness.local}")
    private String adminEmail;

    @Override
    public void run(String... args) {
        if (!bootstrapAdminEnabled) {
            return;
        }

        if (adminPassword == null || adminPassword.isBlank()) {
            log.warn("Bootstrap admin abilitato ma password non configurata. Admin non creato.");
            return;
        }

        if (!istruttoreDAO.existsByUsername(adminUsername)) {
            Istruttore admin = new Istruttore();
            admin.setUsername(adminUsername);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setEmail(adminEmail);
            admin.setNome("Admin");
            admin.setCognome("Sistema");
            admin.setRuolo(Ruolo.ADMIN);
            admin.setDataNascita(LocalDate.of(1980, 1, 1));
            admin.setTelefono("0000000000");

            istruttoreDAO.save(admin);
            log.warn("Admin bootstrap creato per username: {}", adminUsername);
        }
    }
}
