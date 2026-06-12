package com.itscadmo.centro_fitness.Data.Entity;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "utente_base")
@Data
public abstract class Utente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "cognome", nullable = false)
    private String cognome;

    @Column(name = "data_nascita")
    private LocalDate dataNascita;
    @Enumerated(EnumType.STRING)
    @Column(name = "genere")
    private Genere genere;
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "telefono")
    private String telefono;

    @Embedded
    @Column(name = "indirizzo")
    private Indirizzo indirizzo;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "ruolo")
    private Ruolo ruolo;

    public static enum Ruolo {
        ADMIN,
        CLIENTE,
        ISTRUTTORE
    }
    
    

}
