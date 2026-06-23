package com.esports.sanctionservice.models.dtos;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;


@Getter
@Setter
@ToString
@NoArgsConstructor
public class SancionDTO {

    private Long usuarioId;
    private Long equipoId;

    @NotBlank(message = "el motivo es obligatorio")
    private String motivo;

    @NotNull(message = "la fecha de inicio es obligatoria ")
    private LocalDateTime fechaInicio;

    @NotNull(message = "la fecha de fin es obligatoria")
    private LocalDateTime fechaFin;

    @NotBlank(message = "la severidad es obligatoria")
    private String severidad;
}
