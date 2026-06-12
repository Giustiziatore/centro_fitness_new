package com.itscadmo.centro_fitness.DTO.Auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RispostaAutenticazione {

    private String tokenAccesso;
    private String tokenRefresh;
    private String tipoToken = "Bearer";
    private Long scadeIn;
    private String nomeUtente;
    private String email;
}