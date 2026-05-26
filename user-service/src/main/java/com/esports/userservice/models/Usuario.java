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

    // el nickname es unico por ejemplo: juanitoElPro777
    @NotBlank(message = "El nickname no puede estar vacio")
    @Column(nullable = false, unique = true)
    private String nickname;

    @NotBlank(message = "El email no puede estar vacio")
    @Email(message = "El email debe tener formato valido")
    @Column(nullable = false, unique = true)
    private String email;

    // rol del usuario ejemplo JUGADOR, ORGANIZADOR, ADMINISTRADOR
    @NotNull(message = "el rol es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

    // el estado ejemplo: activo, inactivo, sancionado
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Estado estado = Estado.ACTIVO;

    // roles que pueden tener el sistema
    public enum Rol{
        JUGADOR,
        ORGANIZADOR,
        ADMINISTRADOR
    }

    // posible estado del usuario
    public enum Estado{
        ACTIVO,
        INACTIVO,
        SANCIONADO
    }

}
