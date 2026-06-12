package com.itscadmo.centro_fitness.DTO.Auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RichiestaCambioPassword {

    @NotBlank(message = "La password attuale è obbligatoria")
    private String vecchiaPassword;

    @NotBlank(message = "La nuova password è obbligatoria")
    @Size(min = 8, message = "La password deve contenere almeno 8 caratteri")
    private String nuovaPassword;
}