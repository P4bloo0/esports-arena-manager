package com.esports.notificationservice.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "notificaciones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notificacion_id")
    private Long notificacionId;


    @Column(name = "usuario_id")
    private Long usuarioId;


    @Column(name = "equipo_id")
    private Long equipoId;


    @NotNull(message = "El tipo de notificacion es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoNotificacion tipo;


    @NotBlank(message = "El mensaje es obligatorio")
    @Column(nullable = false)
    private String mensaje;


    @Column(nullable = false)
    private Boolean leida = false;


    @Column(nullable = false)
    private LocalDateTime fecha = LocalDateTime.now();


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Estado estado = Estado.ACTIVA;


    public enum TipoNotificacion {
        INSCRIPCION,
        PARTIDA,
        RESULTADO,
        SANCION,
        PREMIO
    }

    public enum Estado {
        ACTIVA,
        ARCHIVADA
    }
}
