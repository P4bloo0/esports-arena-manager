package com.esports.teamservice.models.dtos;

import jakarta.validation.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Setter
@Getter
@ToString
@NoArgsConstructor
public class MiembroEquipoDTO {

    @NotNull(message = "El equipo es obligatorio")
    private Long equipoId;

    @NotNull(message = "El usuario es obligatorio")
    private Long usuarioId;

    @NotBlank(message = "El rol dentro del equipo es obligatorio")
    private String rolDentroEquipo;

}
