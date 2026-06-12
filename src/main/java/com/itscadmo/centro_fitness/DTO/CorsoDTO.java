package com.itscadmo.centro_fitness.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CorsoDTO {

    private Long idCorso;

    @NotBlank(message = "Nome corso obbligatorio")
    private String nome;

    private String descrizione;

    private String giornoSettimana; // Es: "LUNEDI", "MARTEDI"

    @Min(value = 30, message = "Durata minima: 30 minuti")
    private Integer durataMinuti;

    @NotNull(message = "Sala obbligatoria")
    private Long salaId;

    @NotNull(message = "Istruttore obbligatorio")
    private Long istruttoreId;

    // Campi informativi (opzionali per risposta)
    private String nomeSala;
    private String nomeIstruttore;
    private String cognomeIstruttore;
}