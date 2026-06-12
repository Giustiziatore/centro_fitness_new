package com.itscadmo.centro_fitness.DTO;

import com.itscadmo.centro_fitness.Data.Entity.StatoIscrizione;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class IscrizioneLezioneDTO {

    private Long id;

    @NotNull(message = "Lezione obbligatoria")
    private Long lezioneId;

    @NotNull(message = "Cliente obbligatorio")
    private Long clienteId;

    private LocalDateTime dataIscrizione;

    private StatoIscrizione stato;

    // Campi informativi (per risposta)
    private String nomeCliente;
    private String cognomeCliente;
    private String nomeCorso;
    private LocalDateTime dataOraLezione;
    private String nomeSala;
    private Boolean abbonamentoAttivo;
}