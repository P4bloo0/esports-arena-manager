package com.esports.registrationservice.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "inscripciones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Inscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inscripcion_id")
    private Long inscripcionId;


    @NotNull(message = "El torneo es obligatorio")
    @Column(name = "torneo_id", nullable = false)
    private Long torneoId;


    @Column(name = "equipo_id")
    private Long equipoId;

    @Column(name = "jugador_id")
    private Long jugadorId;


    @NotNull(message = "El tipo de participante es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_participante", nullable = false)
    private TipoParticipante tipoParticipante;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Estado estado = Estado.PENDIENTE;


    @Column(name = "fecha_inscripcion", nullable = false)
    private LocalDateTime fechaInscripcion = LocalDateTime.now();


    public enum TipoParticipante {
        EQUIPO,
        INDIVIDUAL
    }


    public enum Estado {
        PENDIENTE,
        CONFIRMADA,
        CANCELADA
    }
}
