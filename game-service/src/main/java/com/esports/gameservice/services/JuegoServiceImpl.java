package com.esports.gameservice.services;
import com.esports.gameservice.exceptions.JuegoException;
import com.esports.gameservice.models.Juego;
import com.esports.gameservice.models.dtos.JuegoDTO;
import com.esports.gameservice.repositories.JuegoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

// aqui va la logica del negocio y o proyecto
@Service
public class JuegoServiceImpl implements JuegoService {

    @Autowired
    private JuegoRepository juegoRepository;

    @Override
    public List<Juego> findAll(){
        return this.juegoRepository.findAll();
    }

    @Override
    public List<Juego> findAllActivos(){
        return this.juegoRepository.findByEstado(true);
    }

    //Busca el juego por id
    @Override
    public Juego findById(Long id){
        return this.juegoRepository.findById(id).orElseThrow(()-> new JuegoException("El juego con id " + id + " no encontrado"));
    }

    @Override
    public Juego save(JuegoDTO dto) {
        if (this.juegoRepository.findByNombre(dto.getNombre()).isPresent()) {
            throw new JuegoException("Ya existe un juego con ese nombre: " + dto.getNombre());
        }
        Juego juego = new Juego();
        juego.setNombre(dto.getNombre());
        juego.setGenero(dto.getGenero());
        juego.setModalidad(dto.getModalidad());
        juego.setJugadoresPorEquipo(dto.getJugadoresPorEquipo());
        juego.setEstado(true);
        return this.juegoRepository.save(juego);
    }

    // aqui se actualiza un juego por su id
    @Override
    public Juego update(Long id, JuegoDTO dto){
        return this.juegoRepository.findById(id).map(juego -> {
            juego.setModalidad(dto.getModalidad());
            juego.setGenero(dto.getGenero());
            juego.setJugadoresPorEquipo(dto.getJugadoresPorEquipo());
            return this.juegoRepository.save(juego);
        }).orElseThrow(() -> new JuegoException("Juego con id " + id + " no encontrado"));
    }

    @Override
    public Juego desactivar(Long id){
        return this.juegoRepository.findById(id).map(juego -> {
            juego.setEstado(false);
            return this.juegoRepository.save(juego);
        }).orElseThrow(()-> new JuegoException("Juego con id " + id + " no encontrado"));
    }


}
