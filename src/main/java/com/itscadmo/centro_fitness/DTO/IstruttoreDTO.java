package com.itscadmo.centro_fitness.DTO;

import com.itscadmo.centro_fitness.Data.Entity.Genere;
import com.itscadmo.centro_fitness.Data.Entity.Indirizzo;
import com.itscadmo.centro_fitness.Data.Entity.Utente;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@ToString

public class IstruttoreDTO {

    //Campi Utente
    private Long id;

    @NotBlank
    private String nome;

    @NotBlank
    private String cognome;

    @NotBlank
    private String username;

    @NotBlank
    private Utente.Ruolo ruolo;

    private LocalDate dataNascita;

    private Genere genere;

    private String telefono;

    @Email(message = "Formato email non valido")
    @NotBlank
    private String email;
    private Indirizzo indirizzo;


    //Campi Istruttore
    private String specializzazione;

    private String orariDisponibilita;

}
