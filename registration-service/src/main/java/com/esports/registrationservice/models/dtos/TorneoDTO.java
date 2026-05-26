package com.esports.registrationservice.models.dtos;

import lombok.*;
import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TorneoDTO {
    private Long torneoId;
    private String nombre;
    private Integer cupoMaximo;
    private Integer cupoActual;
    private String estado;
    private LocalDateTime fechaCierreInscripcion;
}
