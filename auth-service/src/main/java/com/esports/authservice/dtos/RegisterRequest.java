package com.esports.authservice.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "El nickname es obligatorio")
    private String nickname;

    @NotBlank(message = "La contrasena es obligatoria")
    private String password;

    private Set<String> roles = new HashSet<>();
}
