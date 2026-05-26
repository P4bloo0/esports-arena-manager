package com.esports.teamservice.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

//esto representara a un equipo de jugadores
@Entity
@Getter
@Setter
@Table(name = "equipos")
@ToString
@NoArgsConstructor
public class Equipo {

    //clave primaria
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "equipo_id")
    private Long equipoId;

    //nombre del equipo
    @NotBlank(message = "El nombre del equipo no puede estar vacio")
    @Column(nullable = false, unique = true)
    private String nombre;

    //id del capitan(usuario) del equipo
    @NotNull(message = "El capitan es obligatorio")
    @Column(name = "capitan_id", nullable = false)
    private Long capitanId;

    //id del juego principal del equipo
    @NotNull(message = "El juego principal es obligatorio")
    @Column(name = "juego_principal_id", nullable = false)
    private Long juegoPrincipalId;

    //true activo
    // false desactivado
    @Column(nullable = false)
    private Boolean estado = true;
}
