package com.esports.userservice.repositories;

import com.esports.userservice.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

// habla con la base de datos
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByNickname(String nickname);


    Optional<Usuario> findByEmail(String email);

    List<Usuario> findByRol(Usuario.Rol rol);
    List<Usuario> findByEstado(Usuario.Estado estado);

}
