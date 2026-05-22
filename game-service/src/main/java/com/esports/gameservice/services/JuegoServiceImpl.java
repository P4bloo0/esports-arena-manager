package com.esports.gameservice.services;
import com.esports.gameservice.exceptions.JuegoException;
import com.esports.gameservice.models.Juego;
import com.esports.gameservice.models.dtos.JuegoDTO;
import com.esports.gameservice.repositories.JuegoRepository;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

// aqui va la logica del negocio y o proyecto
@Service
public class JuegoServiceImpl implements JuegoService {

    private static final Logger log = LoggerFactory.getLogger(JuegoServiceImpl.class);

    @Autowired
    private JuegoRepository juegoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Juego> findAll(){
        log.info("Listando todos los juegos");
        return juegoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Juego> findAllActivos(){
        log.info("Listando los juegos que esten activos");
        return juegoRepository.findByEstado(true);
    }

    @Override
    @Transactional(readOnly = true)
    public Juego findById(Long id) {
        log.info("Buscando juego con id: {}", id);
        return juegoRepository.findById(id)
                .orElseThrow(() -> new JuegoException("Juego con id " + id + " no encontrado"));
    }

    @Override
    @Transactional
    public Juego save(JuegoDTO dto){
        log.info("Creando juego: {}", dto.getNombre());

        // el nombre del juego debe de ser unico
        if (juegoRepository.findByNombre(dto.getNombre()).isPresent()){
            throw new JuegoException("Ya eciste un juego con el mismo nombre: " + dto.getNombre());
        }

        // el dto pasa a ser una entidad para ser guardada en la base de datos
        Juego juego = new Juego();
        juego.setNombre(dto.getNombre());
        juego.setGenero(dto.getGenero());
        juego.setModalidad(dto.getModalidad());
        juego.setJugadoresPorEquipo(dto.getJugadoresPorEquipo());
        juego.setEstado(true);

        Juego guardado = juegoRepository.save(juego);
        log.info("Juego creado con id: {}", guardado.getJuegoId());
        return guardado;

    }

    // aqui se actualiza un juego por su id
    @Override
    @Transactional
    public Juego update(Long id, JuegoDTO dto){
        log.info("Actualizando juego con id: {}", id);
        Juego juego = findById(id);
        juego.setModalidad(dto.getModalidad());
        juego.setGenero(dto.getGenero());
        juego.setJugadoresPorEquipo(dto.getJugadoresPorEquipo());
        return juegoRepository.save(juego);
    }

    @Override
    @Transactional
    public Juego desactivar(Long id){
        log.warn("Desactivando juego con id: {}" , id);
        Juego juego = findById(id);
        // juego inactivo no permite torneos nuevos
        juego.setEstado(false);
        return juegoRepository.save(juego);
    }


}
