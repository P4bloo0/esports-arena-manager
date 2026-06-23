package com.esports.tournamentservice.models.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class JuegoDTO { //info del game service

    private Long juegoId;
    private String nombre;
    private String genero;
    private String modalidad;
    private Integer jugadoresPorEquipo;
    private Boolean estado;
}
