package com.esports.userservice.services;

import com.esports.userservice.exceptions.UsuarioException;
import com.esports.userservice.models.Usuario;
import com.esports.userservice.models.dtos.UsuarioDTO;
import com.esports.userservice.repositories.UsuarioRepository;
import jakarta.persistence.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class UsuarioServiceImpl implements UsuarioService{

    private static final Logger log = LoggerFactory.getLogger(UsuarioServiceImpl.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    // lista a todos los usuarios
    @Override
    @Transactional(readOnly = true)
    public List<Usuario> findAll(){
        log.info("Listando a todos los usuarios");
    }

    // busca a los usuarios por su rol
    @Override
    @Transactional(readOnly = true)
    public List<Usuario> findByRol(Usuario.Rol rol){
        log.info("Buscando usuarios con rol {}", rol);
        return usuarioRepository.findByRol(rol);
    }

    // busca usuarios por estado
    @Override
    @Transactional(readOnly = true)
    public  List<Usuario> findByEstado(Usuario.Estado estado){
        log.info("Buscando a los usuarios con estado: {}", estado);
    }

    // busca a los usuarios por su id
    @Override
    @Transactional(readOnly = true)
    public Usuario findById(Long id){
        log.info("Buscando al usuario con id: {}", id);
        return usuarioRepository.findById(id).orElseThrow(() -> new UsuarioException("el usuario con id " + id + " no se ha encontrado"));
    }

    //guardar
    @Override
    @Transactional
    public Usuario save(UsuarioDTO dto){
        log.info("Registrando usuario: {}", dto.getNickname());

        //el nickname es unico
        if(usuarioRepository.findByNickname(dto.getNickname()).isPresent()){
            throw new UsuarioException("el nickname '" + dto.getNickname() + "' ya existe");
        }

        // email unico
        if(usuarioRepository.findByEmail(dto.getEmail()).isPresent()){
            throw new UsuarioException("el email '" + dto.getEmail() + "' ya esta registrado");

        }

        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setNickname(dto.getNickname());
        usuario.setEmail(dto.getEmail());
        usuario.setRol(dto.getRol());
        usuario.setEstado(Usuario.Estado.ACTIVO);

        Usuario guardado = usuarioRepository.save(usuario);
        log.info("Usuario creado con la id: {}", guardado.getUsuarioId());

    }

    // actualizar
    @Override
    @Transactional
    public Usuario update(Long id, UsuarioDTO dto){
        log.info("Actualizando usuario con la id {}", id);
        Usuario usuario = findById(id);
        usuario.setNombre(dto.getNombre());
        usuario.setNickname(dto.getNickname());
        return usuarioRepository.save(usuario);
    }

    //desactivar y o borrar
    @Override
    @Transactional
    public Usuario desactivar(Long id){
        log.warn("Desactivando al usuario con id: {}", id);
        Usuario usuario = findById(id);
        // el usuario que este inactivo no podra competir
        usuario.setEstado(Usuario.Estado.INACTIVO);
        return usuarioRepository.save(usuario);
    }

}
