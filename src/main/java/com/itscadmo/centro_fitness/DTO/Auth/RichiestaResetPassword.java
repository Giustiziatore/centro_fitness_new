package com.itscadmo.centro_fitness.DTO.Auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RichiestaResetPassword {

    @NotNull(message = "L'ID utente è obbligatorio")
    private Long utenteId;

    @NotBlank(message = "La nuova password è obbligatoria")
    @Size(min = 8, message = "La password deve contenere almeno 8 caratteri")
    private String nuovaPassword;
}