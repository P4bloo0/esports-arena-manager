package com.esports.rankingservice.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Entity
@Table(name = "rankings",
       uniqueConstraints = @UniqueConstraint(columnNames = {"torneo_id", "participante_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Ranking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ranking_id")
    private Long rankingId;


    @NotNull(message = "El torneo es obligatorio")
    @Column(name = "torneo_id", nullable = false)
    private Long torneoId;


    @NotNull(message = "El participante es obligatorio")
    @Column(name = "participante_id", nullable = false)
    private Long participanteId;


    @Column(nullable = false)
    private Integer puntos = 0;


    @Column(nullable = false)
    private Integer victorias = 0;

    @Column(nullable = false)
    private Integer derrotas = 0;


    @Column(nullable = false)
    private Integer diferencia = 0;


    @Column(nullable = false)
    private Integer posicion = 0;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Estado estado = Estado.ACTIVO;

    public enum Estado {
        ACTIVO,
        CERRADO
    }
}
