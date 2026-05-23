package com.esports.teamservice.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@Table(name = "miembros_equipo")
@NoArgsConstructor

// este metodo representa un miembro del equipo
public class MiembroEquipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "miembro_id")
    private Long miembroId;

    //id del equipo al que pertenece
    @NotNull(message = "El equipo es obligatorio")
    @Column(name = "equipo_id", nullable = false)
    private Long equipoId;

    //id del usuario que es miembro del equipo
    @NotNull(message = "El usuario es obligatorio")
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    //rol dentro del equipo capitan, suplente, titular
    @NotBlank(message = "El rol dentro del equipo es obligatorio")
    @Column(name = "rol_dentro_equipo", nullable = false)
    private String rolDentroEquipo;
}
