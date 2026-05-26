package com.esports.notificationservice.models.dtos;

import com.esports.notificationservice.models.Notificacion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionDTO {


    private Long usuarioId;
    private Long equipoId;

    @NotNull(message = "El tipo de notificacion es obligatorio")
    private Notificacion.TipoNotificacion tipo;

    @NotBlank(message = "El mensaje es obligatorio")
    private String mensaje;
}
