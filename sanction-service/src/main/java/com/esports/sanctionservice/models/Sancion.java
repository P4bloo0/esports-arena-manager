package com.esports.sanctionservice.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

//sancion para jugador o equipo
@Entity
@Table(name = "sanciones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Sancion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sancion_id")
    private Long sancionId;

    //jugador
    @Column(name = "usuario_id")
    private Long usuarioId;

    //equipo
    @Column(name = "equipo_id")
    private Long equipoId;

    @NotBlank(message = "el motivo es obligatorio")
    @Column(nullable = false)
    private String motivo;

    @NotNull(message = "la fecha de inicio es obligatoria")
    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;

    @NotNull(message = "la fecha de fin es obligatoria")
    @Column(name = "fecha_fin", nullable = false)
    private LocalDateTime fechaFin;

    @Column(nullable = false)
    private String estado = "ACTIVA";

    @NotBlank(message = "la severidad es obligatoria")
    @Column(nullable = false)
    private String severidad;

}
