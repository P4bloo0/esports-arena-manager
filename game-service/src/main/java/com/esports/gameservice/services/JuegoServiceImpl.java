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
    }


}
