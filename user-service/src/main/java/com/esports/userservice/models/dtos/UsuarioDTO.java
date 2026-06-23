package com.esports.userservice.models.dtos;

import com.esports.userservice.models.Usuario;
import jakarta.validation.constraints.*;
import lombok.*;

//objeto que recibe el controller cuando alguien hace post o put
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {

    @NotBlank(message = "El nombre no puede estar vacio")
    private String nombre;

    @NotBlank(message = "el nickname no puede estar vacio")
    private String nickname;

    @NotBlank(message = "El email no puede estar vacio")
    @Email(message = "EL email debe tener formato valido")
    private String email;

    @NotNull(message = "El rol es obligatorio")
    private Usuario.Rol rol;
}
