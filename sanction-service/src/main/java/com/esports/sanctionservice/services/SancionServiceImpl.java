package com.esports.sanctionservice.services;

import com.esports.sanctionservice.exceptions.SancionException;
import com.esports.sanctionservice.models.Sancion;
import com.esports.sanctionservice.models.dtos.SancionDTO;
import com.esports.sanctionservice.repositories.SancionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SancionServiceImpl implements SancionService {

    @Autowired
    private SancionRepository sancionRepository;


    //devuelve todas las sanciones
    @Transactional(readOnly = true)
    @Override
    public List<Sancion> findAll(){
        return this.sancionRepository.findAll();
    }

    //busca alguna sancion con la id pero si no existe manda una excepcion
    @Transactional(readOnly = true)
    @Override
    public Sancion findById(Long id){
        return this.sancionRepository.findById(id)
                .orElseThrow(() -> new SancionException("Sancion con la id " + id + " no encontrada",404));
    }

    //devuelve todas las sanciones de un solo usuario
    @Transactional(readOnly = true)
    @Override
    public List<Sancion> findByUsuarioId(Long usuarioId){
        return this.sancionRepository.findByUsuarioId(usuarioId);
    }

    //esto devolvera todas las sanciones de un equipo
    @Transactional(readOnly = true)
    @Override
    public List<Sancion> findByEquipoId(Long equipoId){
        return this.sancionRepository.findByEquipoId(equipoId);
    }

    //esto creara una nueva sancion
    @Transactional
    @Override
    public Sancion save(SancionDTO dto){
        // esto hara que tenga al menos un usuario o equipo
        if(dto.getUsuarioId() == null && dto.getEquipoId() == null){
            throw new SancionException("La sancion debe de tener al menos un usuario o equipo");
        }

        //la fecha final debe de ser posterior a la fecha de inicio
        if(!dto.getFechaFin().isAfter(dto.getFechaInicio())){
            throw new SancionException("La fecha de fin debe ser posterior a la fecha de inicio");
        }
        Sancion sancion = new Sancion();
        sancion.setUsuarioId(dto.getUsuarioId());
        sancion.setEquipoId(dto.getEquipoId());
        sancion.setMotivo(dto.getMotivo());
        sancion.setFechaInicio(dto.getFechaInicio());
        sancion.setFechaFin(dto.getFechaFin());
        sancion.setSeveridad(dto.getSeveridad());
        sancion.setEstado("ACTIVA");
        return this.sancionRepository.save(sancion);

    }

    //esto actualizara los datos de una sancion existente
    @Transactional
    @Override
    public Sancion update(Long id, SancionDTO dto){
        return this.sancionRepository.findById(id).map(sancion -> {
            //esto hara que no se pueda modificar una sancion cerrada
            if(sancion.getEstado().equals("CERRADA")){
                throw new SancionException("No se puede modificar una sancion cerrada");
            }
            if(!dto.getFechaFin().isAfter(sancion.getFechaInicio())) throw new SancionException("El fin debe ser posterior al inicio");
            sancion.setMotivo(dto.getMotivo());
            sancion.setFechaFin(dto.getFechaFin());
            sancion.setSeveridad(dto.getSeveridad());
            return this.sancionRepository.save(sancion);
        }).orElseThrow(()-> new SancionException("La sancion con id" + id + " no encontrada",404));
    }

    //cierra una sancion cerrada
    @Transactional
    @Override
    public Sancion cerrar(Long id){
        return this.sancionRepository.findById(id).map(sancion -> {
            sancion.setEstado("CERRADA");
            return this.sancionRepository.save(sancion);

        }).orElseThrow(() -> new SancionException("La sancion con la id " + id + " no encontrada",404));
    }

    //esto verificara si un usuario o equipo tiene una sancion activa
    private boolean vigente(Sancion s) { var now=com.esports.common.ArenaTime.now(); return !now.isBefore(s.getFechaInicio()) && !now.isAfter(s.getFechaFin()); }
    @Transactional(readOnly=true)
    @Override
    public boolean tieneSancionActiva(Long usuarioId, Long equipoId){
        if(usuarioId != null && this.sancionRepository.findByUsuarioIdAndEstado(usuarioId, "ACTIVA").stream().anyMatch(this::vigente))
            return true;
        if(equipoId != null && this.sancionRepository.findByEquipoIdAndEstado(equipoId, "ACTIVA").stream().anyMatch(this::vigente))
            return true;
        return false;
    }

}
