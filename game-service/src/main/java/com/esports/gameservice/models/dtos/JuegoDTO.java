package com.esports.gameservice.models.dtos;

import jakarta.validation.constraints.*;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JuegoDTO {

    @NotBlank(message = "El nombre del juego no puede estar vacio")
    private String nombre;

    @NotBlank(message = "EL genero no puede estar vacio")
    private String genero;

    @NotBlank(message = "La modalidad no puede estar vacia")
    private String modalidad;

    @NotNull(message = "La cantidad de juagdores es obligatoria")
    @Min(value = 1, message = "Debe de haber al menos 1 jugador por equipo")
    private Integer jugadoresPorEquipo;
}
