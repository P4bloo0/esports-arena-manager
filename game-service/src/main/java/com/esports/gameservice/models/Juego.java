package com.esports.gameservice.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

// bd :vv
@Entity
@Table(name = "juego")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Juego {

    // clave
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "juego_id")
    private Long juegoId;

    @NotBlank(message = "EL nombre del juego no puede estar vacio")
    @Column(nullable = false)
    private String nombre;

    @NotBlank(message = "EL genero del juego no puede estar vacio")
    @Column(nullable = false)
    private String genero;

    @NotBlank(message = "La modalidad no puede estar vacia")
    @Column(nullable = false)
    private String modalidad;

    @Min(value = 1, message = "Debe haber al menos 1 jugador por equipo")
    @Column (name = "jugadores_por_equipo", nullable = false)
    private Integer jugadoresPorEquipo;

    @Column(nullable = false)
    private Boolean estado = true;


}
