package com.itscadmo.centro_fitness.DTO.Auth;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RichiestaAccesso {

    @NotBlank(message = "Il nome utente è obbligatorio")
    private String username;

    @NotBlank(message = "La password è obbligatoria")
    private String password;
}