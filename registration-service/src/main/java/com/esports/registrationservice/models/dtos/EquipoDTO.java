package com.esports.registrationservice.models.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EquipoDTO {
    private Long equipoId;
    private Long juegoPrincipalId;
    private Long capitanId;
    private String nombre;
    private Boolean estado;
}