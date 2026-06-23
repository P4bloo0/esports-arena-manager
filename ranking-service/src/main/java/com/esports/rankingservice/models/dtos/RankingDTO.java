package com.esports.rankingservice.models.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RankingDTO {

    @NotNull(message = "El torneo es obligatorio")
    private Long torneoId;

    @NotNull(message = "El participante es obligatorio")
    private Long participanteId;
}
