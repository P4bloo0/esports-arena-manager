package com.esports.teamservice.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name = "equipos")
@ToString
@NoArgsConstructor
public class Equipo {

    //clave
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "equipo_id")
    private Long equipoId;

    //equipo
    @NotBlank(message = "El nombre del equipo no puede estar vacio")
    @Column(nullable = false, unique = true)
    private String nombre;

    //id capitan
    @NotNull(message = "El capitan es obligatorio")
    @Column(name = "capitan_id", nullable = false)
    private Long capitanId;


    @NotNull(message = "El juego principal es obligatorio")
    @Column(name = "juego_principal_id", nullable = false)
    private Long juegoPrincipalId;

    @Column(nullable = false)
    private Boolean estado = true;
}
