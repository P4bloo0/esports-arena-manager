package com.esports.matchservice.models.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PartidaDTO {

    @NotNull(message = "El torneo es obligatorio")
    private Long torneoId;

    @NotNull(message = "El participante A es obligatorio")
    private Long participanteAId;

    @NotNull(message = "El participante B es obligatorio")
    private Long participanteBId;

    @NotBlank(message = "La ronda es obligatoria")
    private String ronda;

    private LocalDateTime fechaHora;
}
