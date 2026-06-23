package com.esports.teamservice.models.dtos;

import lombok.*;

//esto representará la información del usuario que viene de user-service
@Getter
@Setter
@ToString
@NoArgsConstructor
public class UsuarioDTO {
    private Long usuarioId;
    private String nombre;
    private String nickname;
    private String email;
    private String rol;
    private String estado;
}
