package com.itscadmo.centro_fitness.Data.Entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DurataAbbonamento {

    MENSILE(40.0),
    TRIMESTRALE(100.0),
    SEMESTRALE(180.0),
    ANNUALE(250.0);

    private  final  double prezzo;


}
