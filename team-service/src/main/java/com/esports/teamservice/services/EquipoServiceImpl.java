package com.esports.teamservice.services;

import com.esports.teamservice.clients.JuegoClient;
import com.esports.teamservice.clients.UsuarioClient;
import com.esports.teamservice.exceptions.EquipoException;
import com.esports.teamservice.models.Equipo;
import com.esports.teamservice.models.MiembroEquipo;
import com.esports.teamservice.models.dtos.EquipoDTO;
import com.esports.teamservice.models.dtos.MiembroEquipoDTO;
import com.esports.teamservice.repositories.EquipoRepository;
import com.esports.teamservice.repositories.MiembroEquipoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import feign.FeignException;

import java.util.List;


@Service
public class EquipoServiceImpl implements EquipoService{

    @Autowired
    private EquipoRepository equipoRepository;

    @Autowired
    private MiembroEquipoRepository miembroEquipoRepository;

    @Autowired
    private UsuarioClient usuarioClient;

    @Autowired
    private JuegoClient juegoClient;

    @Override //esto devolverá todos los equipos
    public List<Equipo> findAll(){
        return this.equipoRepository.findAll();
    }

    @Override//busca un equipo con la id si no existe lanza una excepción
    public Equipo findById(Long id){
        return this.equipoRepository.findById(id)
                .orElseThrow(()-> new EquipoException("Equipo con id '" + id + "' no encontrado"));
    }

    @Override//esto devolverá equipos por estado
    public List<Equipo> findByEstado( Boolean estado){
        return this.equipoRepository.findByEstado(estado);
    }

    @Override//esto va a crear un nuevo equipo y verificará si existe un capitán
    public Equipo save(EquipoDTO dto){

        if(this.equipoRepository.findByNombre(dto.getNombre()).isPresent()){
            throw new EquipoException("Ya existe un equipo con ese nombre: " + dto.getNombre());
        }
        //esto verificará si el capitán existe en user-service
        try{
            this.usuarioClient.getUsuarioById(dto.getCapitanId());
        }catch (FeignException e){
            throw new EquipoException("El usuario capitán con la id '" + dto.getCapitanId() + "' no existe");
        }
        //esto verificará si el juego existe en el game-service
        try{
            this.juegoClient.getJuegoById(dto.getJuegoPrincipalId());
        }catch (FeignException e){
            throw new EquipoException("El juego con la id '" + dto.getJuegoPrincipalId() + "' no existe");
        }
        Equipo equipo = new Equipo();
        equipo.setNombre(dto.getNombre());
        equipo.setCapitanId(dto.getCapitanId());
        equipo.setJuegoPrincipalId(dto.getJuegoPrincipalId());
        equipo.setEstado(true);
        return this.equipoRepository.save(equipo);
    }

    @Override//esto va a actualizar los datos del equipo existente
    public Equipo update(Long id, EquipoDTO dto){
        return this.equipoRepository.findById(id).map(equipo -> {
            equipo.setNombre(dto.getNombre());
            equipo.setCapitanId(dto.getCapitanId());
            return this.equipoRepository.save(equipo);
        }).orElseThrow(() -> new EquipoException("Equipo con id '" + id + " no encontrado"));
    }


    @Override// esto desactiva un equipo
    public Equipo desactivar(Long id){
        return this.equipoRepository.findById(id).map(equipo -> {
            equipo.setEstado(false);
            return this.equipoRepository.save(equipo);
        }).orElseThrow(() -> new EquipoException("Equipo con id '" + id + "' no encontrado"));
    }


    @Override//esto agregara miembros al equipo
    public MiembroEquipo agregarMiembro(MiembroEquipoDTO dto){

        Equipo equipo = findById(dto.getEquipoId());

        if(!equipo.getEstado()){
            throw new EquipoException("El equipo esta inactivo y no puede agregar miembros.");
        }
        try{
            this.usuarioClient.getUsuarioById(dto.getUsuarioId());
        }catch (FeignException e){
            throw new EquipoException("El usuario con id '" + dto.getUsuarioId() + "' no existe");
        }

        if (this.miembroEquipoRepository.findByEquipoIdAndUsuarioId(dto.getEquipoId(), dto.getUsuarioId()).isPresent()){
            throw new EquipoException("El usuario ya es miembro de este equipo");
        }
        MiembroEquipo miembro = new MiembroEquipo();
        miembro.setEquipoId(dto.getEquipoId());
        miembro.setUsuarioId(dto.getUsuarioId());
        miembro.setRolDentroEquipo(dto.getRolDentroEquipo());
        return this.miembroEquipoRepository.save(miembro);

        }

        //esto retornara a todos los miembros del equipo
    @Override
    public List<MiembroEquipo> findMiembrosByEquipoId(Long equipoId){
        return this.miembroEquipoRepository.findByEquipoId(equipoId);
    }


}
