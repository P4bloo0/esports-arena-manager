package com.esports.registrationservice.models.dtos;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EquipoDTO {
    private Long equipoId;
    private String nombre;
    private String estado;
}
