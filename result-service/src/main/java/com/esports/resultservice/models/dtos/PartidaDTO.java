package com.esports.resultservice.models.dtos;

import lombok.*;

// representa los datos de la partida recibidos desde match-service
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
