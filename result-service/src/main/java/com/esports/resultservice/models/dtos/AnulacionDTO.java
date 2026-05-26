package com.esports.resultservice.models.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

// objeto que recibe el controller cuando se quiere anular un resultado
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AnulacionDTO {

    @NotBlank(message = "La justificacion de anulacion es obligatoria")
    private String justificacion;
}
