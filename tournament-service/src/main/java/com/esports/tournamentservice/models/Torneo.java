package com.esports.tournamentservice.models;

import jakarta.persistence.*;
import jakarta.validation.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;

@Table(name = "torneos")
@ToString
@Getter
@Setter
@NoArgsConstructor
public class Torneo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "torneo_id")
    private Long torneoId;

    @NotBlank(message = "El nombre del torneo es obligatorio")
    @Column(nullable = false)
    private String nombre;

    @NotNull(message = "El juego es obligatorio")
    @Column(name = "juego_id", nullable = false)
    private Long juegoId;

    @NotNull(message = "La fecha de inicio es obligatoria")
    @Column(name = "fecha_inicio")
    private LocalDateTime fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    @Column(name = "fecha_fin")
    private LocalDateTime fechaFin;

    @Column(name = "fecha_cierre_inscripcion")//fecha hasta la que se puede inscribir
    private LocalDateTime fechaCierreInscripcion;

    @Min(value = 2, message = "El cupo maximo debe ser al menos 2")//cupo maximo de participantes
    @Column(name = "cupo_maximo", nullable = false)
    private Integer cupoMaximo;

    @Column(name = "cupo_actual")//cuantos estan inscritos
    private Integer cupoActual = 0;

    @Column(nullable = false)//estados posibles
    private String estado = "BORRADOR";

    @NotBlank(message = "La modalidad es obligatoria")//modalidad_ eliminacion_directa, grupos etc...
    @Column(nullable = false)
    private String modalidad;
}
