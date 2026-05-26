package com.esports.rankingservice.models.dtos;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PartidaDTO {
    private Long partidaId;
    private Long torneoId;
    private Long participanteAId;
    private Long participanteBId;
    private String estado;
}
