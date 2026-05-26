package com.esports.resultservice.models.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

// objeto que recibe el controller cuando alguien hace POST para registrar un resultado
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResultadoDTO {

    @NotNull(message = "La partida es obligatoria")
    private Long partidaId;

    @NotNull(message = "El ganador es obligatorio")
    private Long ganadorId;

    @NotNull(message = "El puntaje A es obligatorio")
    @Min(value = 0, message = "El puntaje A no puede ser negativo")
    private Integer puntajeA;

    @NotNull(message = "El puntaje B es obligatorio")
    @Min(value = 0, message = "El puntaje B no puede ser negativo")
    private Integer puntajeB;
}
