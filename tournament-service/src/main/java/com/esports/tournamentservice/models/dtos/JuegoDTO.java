package com.esports.tournamentservice.models.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class JuegoDTO { //esto representa la info del juego que viene del game-service

    private Long juegoId;
    private String nombre;
    private String genero;
    private String modalidad;
    private Integer jugadoresPorEquipo;
    private Boolean estado;
}
