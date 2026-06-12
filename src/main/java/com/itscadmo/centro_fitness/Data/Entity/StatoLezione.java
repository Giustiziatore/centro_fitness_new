package com.itscadmo.centro_fitness.Data.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatoLezione {
    PROGRAMMATA("Lezione programmata, iscrizioni aperte"),
    IN_CORSO("Lezione in svolgimento"),
    COMPLETATA("Lezione completata"),
    CANCELLATA("Lezione cancellata");

    private final String descrizione;


}