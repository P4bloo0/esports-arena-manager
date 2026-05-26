package com.esports.rankingservice.models.dtos;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResultadoDTO {
    private Long resultadoId;
    private Long partidaId;
    private Long ganadorId;
    private Integer puntajeA;
    private Integer puntajeB;
    private String estadoValidacion;
}
