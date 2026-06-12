package com.itscadmo.centro_fitness.DTO;

import com.itscadmo.centro_fitness.Data.Entity.DurataAbbonamento;
import com.itscadmo.centro_fitness.Data.Entity.StatoAbbonamento;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AbbonamentoDTO {

    private Long idAbbonamento;

    @NotNull(message = "Cliente ID obbligatorio")
    private Long clienteId;

    @NotNull(message = "Durata abbonamento obbligatoria")
    private DurataAbbonamento durata;

    // Campi calcolati/gestiti dal service (non obbligatori in input)
    private LocalDate dataInizio;
    private LocalDate dataFine;
    private StatoAbbonamento stato;
    private Double prezzo; // Calcolato in base alla durata
}