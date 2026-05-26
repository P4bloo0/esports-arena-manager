package com.esports.matchservice.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "partidas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Partida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "partida_id")
    private Long partidaId;


    @NotNull(message = "El torneo es obligatorio")
    @Column(name = "torneo_id", nullable = false)
    private Long torneoId;


    @NotNull(message = "El participante A es obligatorio")
    @Column(name = "participante_a_id", nullable = false)
    private Long participanteAId;


    @NotNull(message = "El participante B es obligatorio")
    @Column(name = "participante_b_id", nullable = false)
    private Long participanteBId;


    @NotNull(message = "La ronda es obligatoria")
    @Column(nullable = false)
    private String ronda;


    @Column(name = "fecha_hora")
    private LocalDateTime fechaHora;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Estado estado = Estado.PROGRAMADA;


    public enum Estado {
        PROGRAMADA,
        EN_CURSO,
        FINALIZADA,
        CANCELADA
    }
}
