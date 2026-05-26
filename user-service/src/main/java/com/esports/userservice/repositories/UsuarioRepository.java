package com.esports.userservice.repositories;

import com.esports.userservice.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

// habla con la base de datos
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // busca los usuarios por su nickname
    Optional<Usuario> findByNickname(String nickname);

    // busca los usuarios por email para que no se repitan
    Optional<Usuario> findByEmail(String email);

    // esto lista a los usuarios por rol
    List<Usuario> findByRol(Usuario.Rol rol);

    // esto lista a los usuarios por el estado
    List<Usuario> findByEstado(Usuario.Estado estado);

}
