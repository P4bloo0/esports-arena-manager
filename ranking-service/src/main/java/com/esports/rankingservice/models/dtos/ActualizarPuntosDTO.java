package com.esports.rankingservice.models.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarPuntosDTO {

    @NotNull(message = "Los puntos son obligatorios")
    private Integer puntos;

    @NotNull(message = "Las victorias son obligatorias")
    private Integer victorias;

    @NotNull(message = "Las derrotas son obligatorias")
    private Integer derrotas;

    @NotNull(message = "La diferencia es obligatoria")
    private Integer diferencia;
}
