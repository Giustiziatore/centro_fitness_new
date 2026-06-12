package com.itscadmo.centro_fitness.DTO.Auth;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RichiestaRefreshToken {

    @NotBlank(message = "Il token di refresh è obbligatorio")
    private String tokenRefresh;
}