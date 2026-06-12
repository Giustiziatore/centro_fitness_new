package com.itscadmo.centro_fitness.DTO.Auth;

import com.itscadmo.centro_fitness.Data.Entity.Genere;
import com.itscadmo.centro_fitness.Data.Entity.Indirizzo;
import com.itscadmo.centro_fitness.Data.Entity.Utente.Ruolo;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RichiestaRegistrazione {

    // Campi di Sicurezza e Base 
    @NotBlank(message = "Il nome utente è obbligatorio")
    @Size(min = 3, max = 50, message = "Il nome utente deve essere compreso tra 3 e 50 caratteri")
    private String username;

    @NotBlank(message = "La password è obbligatoria")
    @Size(min = 6, message = "La password deve contenere almeno 6 caratteri")
    private String password;

    @NotBlank(message = "L'email è obbligatoria")
    @Email(message = "L'email deve essere in un formato valido")
    private String email;


    @NotBlank(message = "Il nome è obbligatorio")
    private String nome;

    @NotBlank(message = "Il cognome è obbligatorio")
    private String cognome;

    //Campi opzionali anagrafici
    private LocalDate dataNascita;
    private Genere genere;
    private String telefono;
    private Indirizzo indirizzo;

    //Campo opzionale Cliente
    private String obiettivoFitness;

    private Set<Ruolo> ruoli;

    //Campi opzionali Istruttore
    private String specializzazione;
    private String orariDisponibilita;



}