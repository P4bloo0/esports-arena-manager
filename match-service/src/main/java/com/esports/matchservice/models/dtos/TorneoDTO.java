package com.esports.matchservice.models.dtos;

import lombok.*;

// grande pablo
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TorneoDTO {
    private Long torneoId;
    private String nombre;
    private String estado;
}
