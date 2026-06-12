package com.itscadmo.centro_fitness.Data.Entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Indirizzo {
    
    private String cap;
    private String citta;
    private String via;
    private String numeroCivico;

}
