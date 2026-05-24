package com.esports.tournamentservice.models.dtos;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter
@ToString
@NoArgsConstructor
public class TorneoDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotNull(message = "El juego es obligatorio")
    private Long juegoid;

    @NotNull(message = "La fecha inicio es obligatoria")
    private LocalDateTime fechaInicio;

    @NotNull(message = "La fecha fin es obligatoria")
    private LocalDateTime fechaFin;

    @NotNull(message = "La fecha cierre inscripcion es obligatoria")
    private LocalDateTime fechaCierreInscripcion;

    @Min(value = 2, message = "Cupo minimo es 2")
    private int cupoMaximo;

    @NotBlank(message = "La modalidad es obligatoria")
    private String modalidad;
}
