package com.itscadmo.centro_fitness.DTO;

import com.itscadmo.centro_fitness.Data.Entity.Genere;
import com.itscadmo.centro_fitness.Data.Entity.Indirizzo;
import com.itscadmo.centro_fitness.Data.Entity.IscrizioneLezione;
import com.itscadmo.centro_fitness.Data.Entity.Utente;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@ToString
public class ClienteDTO {

    //Campi Utente
    private Long id;

    @NotBlank
    private String nome;

    @NotBlank
    private String cognome;

    @NotBlank
    private String username;

    private Utente.Ruolo ruolo;

    private LocalDate dataNascita;

    private Genere genere;

    private String telefono;

    @Email(message = "Formato email non valido")
    @NotBlank
    private String email;
    private Indirizzo indirizzo;

    //  Campi Cliente
    private String obiettivoFitness;
    private String numeroTessera;


    // Scadenza Tessera
    private LocalDate dataScadenzaTessera;

    // (Tessera OK + Abbonamento Servizi OK)
    private boolean attivo;

    // Informazione aggiuntiva: data di scadenza dell'abbonamento ai servizi piu' recente
    private LocalDate ultimaScadenzaAbbonamentoServizio;

   
    // Iscrizioni alle lezioni
    private Set<IscrizioneLezione> iscrizioni;

}
