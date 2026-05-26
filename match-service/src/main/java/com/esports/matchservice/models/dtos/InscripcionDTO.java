package com.esports.matchservice.models.dtos;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InscripcionDTO {
    private Long inscripcionId;
    private Long torneoId;
    private Long equipoId;
    private Long jugadorId;
    private String tipoParticipante;
    private String estado;
}
