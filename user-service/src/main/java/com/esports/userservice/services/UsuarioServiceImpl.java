package com.esports.userservice.services;

import com.esports.userservice.exceptions.UsuarioException;
import com.esports.userservice.models.Usuario;
import com.esports.userservice.models.dtos.UsuarioDTO;
import com.esports.userservice.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class UsuarioServiceImpl implements UsuarioService{

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    @Override
    public List<Usuario> findAll(){
        return this.usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public List<Usuario> findByRol(Usuario.Rol rol){
        return this.usuarioRepository.findByRol(rol);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Usuario> findByEstado(Usuario.Estado estado){
        return this.usuarioRepository.findByEstado(estado);
    }

    @Transactional(readOnly = true)
    @Override
    public Usuario findById(Long id){
        return this.usuarioRepository.findById(id).orElseThrow(
                () -> new UsuarioException("el usuario con id " + id + " no se ha encontrado")
        );
    }

    @Transactional
    @Override
    public Usuario save(UsuarioDTO dto){
        //el nickname es unico
        if(this.usuarioRepository.findByNickname(dto.getNickname()).isPresent()){
            throw new UsuarioException("el nickname '" + dto.getNickname() + "' ya existe");
        }

        // email unico
        if(this.usuarioRepository.findByEmail(dto.getEmail()).isPresent()){
            throw new UsuarioException("el email '" + dto.getEmail() + "' ya esta registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setNickname(dto.getNickname());
        usuario.setEmail(dto.getEmail());
        usuario.setRol(dto.getRol());
        usuario.setEstado(Usuario.Estado.ACTIVO);

        return this.usuarioRepository.save(usuario);
    }

    @Transactional
    @Override
    public Usuario update(Long id, UsuarioDTO dto){
        return this.usuarioRepository.findById(id).map(usuario -> {
            usuario.setNombre(dto.getNombre());
            usuario.setNickname(dto.getNickname());
            return this.usuarioRepository.save(usuario);
        }).orElseThrow(
                () -> new UsuarioException("Usuario con id " + id + " no encontrado")
        );
    }

    @Transactional
    @Override
    public Usuario desactivar(Long id){
        return this.usuarioRepository.findById(id).map(usuario -> {
            usuario.setEstado(Usuario.Estado.INACTIVO);
            return this.usuarioRepository.save(usuario);
        }).orElseThrow(
                () -> new UsuarioException("Usuario con id " + id + " no encontrado")
        );
    }

}
