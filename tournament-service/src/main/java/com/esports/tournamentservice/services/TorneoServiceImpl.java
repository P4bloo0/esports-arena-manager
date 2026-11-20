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
    @Autowired private CierreTorneo cierre;

    @Transactional(readOnly = true)
    @Override//esto retorna todos los torneos que hayan
    public List<Torneo> findAll(){
        return this.torneoRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Override//esto busca torneos por su id y tiene una excepcion cuando no existan
    public Torneo findById(Long id){
        return this.torneoRepository.findById(id)
                .orElseThrow(()-> new TorneoException("Torneo con id '" + id + "' no encontrado",404));
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
            if(java.util.Set.of("EN_CURSO","FINALIZADO").contains(torneo.getEstado())){
              throw new TorneoException("No se puede modificar un torneo EN_CURSO o FINALIZADO");
            }

            if(!dto.getFechaInicio().isAfter(dto.getFechaCierreInscripcion()) || !dto.getFechaFin().isAfter(dto.getFechaInicio())) throw new TorneoException("Revisa el orden de las fechas");
            if(!java.util.Objects.equals(torneo.getJuegoId(),dto.getJuegoId())) throw new TorneoException("El juego del torneo no puede cambiarse");
            torneo.setNombre(dto.getNombre());
            torneo.setFechaInicio(dto.getFechaInicio());
            torneo.setFechaFin(dto.getFechaFin());
            torneo.setFechaCierreInscripcion(dto.getFechaCierreInscripcion());
            torneo.setCupoMaximo(dto.getCupoMaximo());
            torneo.setModalidad(dto.getModalidad());
            return this.torneoRepository.save(torneo);
        }).orElseThrow(()->new TorneoException("Torneo con id '" + id + "' no encotrado",404));
    }

    //este metodo va a cambiar el estado de un torneo
    @Transactional
    @Override
    public Torneo cambiarEstado(Long id, String nuevoEstado){
        if("FINALIZADO".equals(nuevoEstado)) return cierre.finalizar(id);
        return this.torneoRepository.findById(id).map(torneo -> {
            if(!(torneo.getEstado().equals("BORRADOR") && nuevoEstado.equals("ABIERTO") || torneo.getEstado().equals("ABIERTO") && nuevoEstado.equals("EN_CURSO"))) throw new TorneoException("Transicion de torneo no permitida");
            torneo.setEstado(nuevoEstado);
            return this.torneoRepository.save(torneo);
        }).orElseThrow(()->new TorneoException("Torneo con id '" + id + "' no encontrado",404));
    }

    @Transactional
    @Override// esto es para borrar un torneo por su id
    public void deleteById(Long id){
        var t=findById(id); if(!"BORRADOR".equals(t.getEstado())) throw new TorneoException("Solo se pueden eliminar borradores");
        this.torneoRepository.deleteById(id);
    }

}
