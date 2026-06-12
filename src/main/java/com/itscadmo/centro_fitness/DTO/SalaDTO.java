package com.itscadmo.centro_fitness.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SalaDTO {

    private Long idSala;

    @NotBlank(message = "Nome sala obbligatorio")
    private String nome;

    @Min(value = 1, message = "Capienza minima: 1 persona")
    private int capienza;

    private String orariApertura;
}