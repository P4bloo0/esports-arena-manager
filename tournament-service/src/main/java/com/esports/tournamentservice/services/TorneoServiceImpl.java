package com.esports.tournamentservice.services;

import com.esports.tournamentservice.clients.JuegoClient;
import com.esports.tournamentservice.exceptions.TorneoException;
import com.esports.tournamentservice.models.Torneo;
import com.esports.tournamentservice.models.dtos.TorneoDTO;
import com.esports.tournamentservice.repositories.TorneoRepository;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TorneoServiceImpl implements TorneoService{

    @Autowired
    private TorneoRepository torneoRepository;

    @Autowired
    private JuegoClient juegoClient;

    @Transactional(readOnly = true)
    @Override//esto retorna todos los torneos que hayan
    public List<Torneo> findAll(){
        return this.torneoRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Override//esto busca torneos por su id y tiene una excepcion cuando no existan
    public Torneo findById(Long id){
        return this.torneoRepository.findById(id)
                .orElseThrow(()-> new TorneoException("Torneo con id '" + id + "' no encontrado"));
    }

    @Transactional(readOnly = true)
    @Override//esto devuelve torneos por estado
    public List<Torneo> findByEstado(String estado){
        return this.torneoRepository.findByEstado(estado);
    }

    @Transactional(readOnly = true)
    @Override//esto devolverá torneos por juego
    public List<Torneo> findByJuegoId(Long juegoId){
        return this.torneoRepository.findByJuegoId(juegoId);
    }

    @Transactional
    @Override//esto va a crear un nuevo torneo y verificara si existe el juego
    public  Torneo save(TorneoDTO dto){

        try{
            this.juegoClient.getJuegoById(dto.getJuegoId());
        }catch (FeignException e){
            throw new TorneoException(("El juego con id '" + dto.getJuegoId() + "' no existe"));
        }
        //la fecha de inicio debe ser posterior a la fecha para inscribirse
        if(!dto.getFechaInicio().isAfter(dto.getFechaCierreInscripcion())){
            throw new TorneoException("La fecha de inicio debe de ser porterior al cierre de la inscripcion");
        }
        //la fecha final debe ser posterior a la fecha de inicio
        if(!dto.getFechaFin().isAfter(dto.getFechaInicio())){
            throw new TorneoException("La fecha de fin debe de ser posterior a la fecha de inicio");
        }

        Torneo torneo = new Torneo();
        torneo.setNombre(dto.getNombre());
        torneo.setJuegoId(dto.getJuegoId());
        torneo.setFechaInicio(dto.getFechaInicio());
        torneo.setFechaFin(dto.getFechaFin());
        torneo.setFechaCierreInscripcion(dto.getFechaCierreInscripcion());
        torneo.setCupoMaximo(dto.getCupoMaximo());
        torneo.setCupoActual(0);
        torneo.setModalidad(dto.getModalidad());
        torneo.setEstado("BORRADOR");
        return this.torneoRepository.save(torneo);
    }

    //esto actualizara los datos de un torneo existente
    @Transactional
    @Override
    public Torneo update(Long id, TorneoDTO dto){
        return this.torneoRepository.findById(id).map(torneo -> {

            //este metodo hara que no se pueda modificar un torneo en curs
            if(torneo.getEstado().equals("EN_CURSO")){
              throw new TorneoException(("No se puede modificar un torneo en curso"));
            }

            torneo.setNombre(dto.getNombre());
            torneo.setFechaInicio(dto.getFechaInicio());
            torneo.setFechaFin(dto.getFechaFin());
            torneo.setFechaCierreInscripcion(dto.getFechaCierreInscripcion());
            torneo.setCupoMaximo(dto.getCupoMaximo());
            torneo.setModalidad(dto.getModalidad());
            return this.torneoRepository.save(torneo);
        }).orElseThrow(()->new TorneoException("Torneo con id '" + id + "' no encotrado"));
    }

    //este metodo va a cambiar el estado de un torneo
    @Transactional
    @Override
    public Torneo cambiarEstado(Long id, String nuevoEstado){
        return this.torneoRepository.findById(id).map(torneo -> {
            torneo.setEstado(nuevoEstado);
            return this.torneoRepository.save(torneo);
        }).orElseThrow(()->new TorneoException("Torneo con id '" + id + "' no encontrado"));
    }

    @Transactional
    @Override// esto es para borrar un torneo por su id
    public void deleteById(Long id){
        this.torneoRepository.deleteById(id);
    }

}
