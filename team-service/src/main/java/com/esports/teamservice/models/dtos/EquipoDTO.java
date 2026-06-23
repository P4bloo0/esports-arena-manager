package com.esports.teamservice.models.dtos;

import jakarta.validation.constraints.*;
import lombok.*;

@ToString
@Getter
@Setter
@NoArgsConstructor
public class EquipoDTO {

    @NotBlank(message = "El nombre del equipo no puede estar vacío")
    private String nombre;

    @NotNull(message = "El capitán es obligatorio")
    private Long capitanId;

    @NotNull(message = "El juego principal es obligatorio")
    private Long juegoPrincipalId;
}
