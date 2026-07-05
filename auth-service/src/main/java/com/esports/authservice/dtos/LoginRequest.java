package com.esports.authservice.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginRequest {

    @NotBlank(message = "El nickname es obligatorio")
    private String nickname;

    @NotBlank(message = "La contrasena es obligatoria")
    private String password;
}
