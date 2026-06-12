package com.itscadmo.centro_fitness.DTO;

import com.itscadmo.centro_fitness.Data.Entity.StatoLezione;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class LezioneDTO {

    private Long idLezione;

    @NotNull(message = "Data e ora inizio obbligatorie")
    @Future(message = "La lezione deve essere programmata in futuro")
    private LocalDateTime dataOraInizio;

    @NotNull(message = "Data e ora fine obbligatorie")
    private LocalDateTime dataOraFine;

    private StatoLezione stato;

    @NotNull(message = "Sala obbligatoria")
    private Long salaId;

    @NotNull(message = "Corso obbligatorio")
    private Long corsoId;

    @NotNull(message = "Almeno un istruttore obbligatorio")
    private List<Long> istruttoriIds;

    // Campi informativi (non obbligatori per la creazione/modifica)
    private String nomeSala;
    private String nomeCorso;
    private Integer capienzaSala;
    private Integer numeroIscritti;
    private Boolean piena;
    private List<String> nomiIstruttori;
}