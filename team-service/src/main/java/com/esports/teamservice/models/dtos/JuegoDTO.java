package com.esports.teamservice.models.dtos;

import lombok.*;

//esto representa la info del juego que viene de game-service
@Getter
@Setter
@NoArgsConstructor
@ToString
public class JuegoDTO {
    private Long juegoId;
    private String nombre;
    private String genero;
    private String modalidad;
    private Integer jugadoresPorEquipo;
    private Boolean estado;
}
