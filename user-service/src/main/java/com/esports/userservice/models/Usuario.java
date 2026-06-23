package com.esports.userservice.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usuario_id")
    private Long usuarioId;

    @NotBlank(message = "El nombre no puede estar vacio")
    @Column(nullable = false)
    private String nombre;

    // nick unico: BillaGameplays
    @NotBlank(message = "El nickname no puede estar vacio")
    @Column(nullable = false, unique = true)
    private String nickname;

    @NotBlank(message = "El email no puede estar vacio")
    @Email(message = "El email debe tener formato valido")
    @Column(nullable = false, unique = true)
    private String email;

    // rol del usuario
    @NotNull(message = "el rol es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Estado estado = Estado.ACTIVO;

//
    public enum Rol{
        JUGADOR,
        ORGANIZADOR,
        ADMINISTRADOR
    }

//posibles estados
    public enum Estado{
        ACTIVO,
        INACTIVO,
        SANCIONADO
    }

}
