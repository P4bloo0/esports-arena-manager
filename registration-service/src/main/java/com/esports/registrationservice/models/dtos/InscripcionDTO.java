package com.esports.registrationservice.models.dtos;

import com.esports.registrationservice.models.Inscripcion;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InscripcionDTO {

    @NotNull(message = "El torneo es obligatorio")
    private Long torneoId;


    private Long equipoId;


    private Long jugadorId;

    @NotNull(message = "El tipo de participante es obligatorio")
    private Inscripcion.TipoParticipante tipoParticipante;
}
