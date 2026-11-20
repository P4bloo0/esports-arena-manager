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
    @jakarta.validation.constraints.Size(min=3,max=16)
    @jakarta.validation.constraints.Pattern(regexp="^[^\\s]+$",message="El apodo no admite espacios")
    private String nickname;
    @NotBlank private String nombre;
    @NotBlank @jakarta.validation.constraints.Email private String email;

    @NotBlank(message = "La contrasena es obligatoria")
    @jakarta.validation.constraints.Size(min=8,max=72)
    private String password;

    private Set<String> roles = new HashSet<>();
}
