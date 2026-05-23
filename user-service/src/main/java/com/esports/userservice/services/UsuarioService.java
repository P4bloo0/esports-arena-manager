package com.esports.userservice.services;

import com.esports.userservice.models.Usuario;
import com.esports.userservice.models.dtos.UsuarioDTO;
import java.util.List;

// esta es una interfaz que define que puede hacer este servicio
// la logica verdadera esta en UsuarioServiceImpl
public interface UsuarioService {

    List<Usuario> findAll();
    List<Usuario> findByRol(Usuario.Rol rol);
    List<Usuario> findByEstado(Usuario.Estado estado);
    Usuario findById(Long id);
    Usuario save(UsuarioDTO dto);
    Usuario update(Long id, UsuarioDTO dto);
    Usuario desactivar(Long id);
}
