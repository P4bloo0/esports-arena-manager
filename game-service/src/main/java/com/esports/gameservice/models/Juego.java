package com.esports.gameservice.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

// lo que hace entity es decirle al spring que la clase es una tabla para la base de datos
@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Juego {

    // Clave primaria
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "juego_id")
    private Long juegoId;

    // esto genera un nombre unico para el juego por ejemplo: minecraft, valorant, robloxito
    @NotBlank(message = "EL nombre del juego no puede estar vacio")
    @Column(nullable = false)
    private String nombre;

    // genero del juego ejemplo: fps o un moba o algun juego de plataformas
    @NotBlank(message = "EL genero del juego no puede estar vacio")
    @Column(nullable = false)
    private String genero;

    // modalidad del juego ejempll: 5v5 o 1vs1 o solitario
    @NotBlank(message = "La modalidad no puede estar vacia")
    @Column(nullable = false)
    private String modalidad;

    // cantidad de jugadores por equipo debe de ser al menos 1
    @Min(value = 1, message = "Debe haber al menos 1 jugador por equipo")
    @Column (name = "jugadores_por_equipo", nullable = false)
    private Integer jugadoresPorEquipo;

    // true = activo | false = desactivado
    @Column(nullable = false)
    private Boolean estado = true;


}
