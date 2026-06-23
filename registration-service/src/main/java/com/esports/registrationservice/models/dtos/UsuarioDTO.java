package com.esports.registrationservice.models.dtos;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {
    private Long usuarioId;
    private String nickname;
    private String estado;
    private String rol;
}
