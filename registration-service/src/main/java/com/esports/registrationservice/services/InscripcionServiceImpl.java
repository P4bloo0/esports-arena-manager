package com.esports.registrationservice.services;

import com.esports.registrationservice.clients.EquipoClient;
import com.esports.registrationservice.clients.SancionClient;
import com.esports.registrationservice.clients.TorneoClient;
import com.esports.registrationservice.clients.UsuarioClient;
import com.esports.registrationservice.exceptions.InscripcionException;
import com.esports.registrationservice.models.Inscripcion;
import com.esports.registrationservice.models.dtos.EquipoDTO;
import com.esports.registrationservice.models.dtos.InscripcionDTO;
import com.esports.registrationservice.models.dtos.TorneoDTO;
import com.esports.registrationservice.models.dtos.UsuarioDTO;
import com.esports.registrationservice.repositories.InscripcionRepository;
import feign.FeignException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class InscripcionServiceImpl implements InscripcionService {

    // log para trazabilidad de operaciones importantes
    private static final Logger log = LoggerFactory.getLogger(InscripcionServiceImpl.class);

    @Autowired
    private InscripcionRepository inscripcionRepository;

    @Autowired
    private TorneoClient torneoClient;

    @Autowired
    private UsuarioClient usuarioClient;

    @Autowired
    private EquipoClient equipoClient;

    @Autowired
    private SancionClient sancionClient;

    // devuelve todas las inscripciones registradas
    @Transactional(readOnly = true)
    @Override
    public List<Inscripcion> findAll() {
        log.info("Consultando todas las inscripciones");
        return this.inscripcionRepository.findAll();
    }

    // busca una inscripcion por su id, lanza excepcion si no existe
    @Transactional(readOnly = true)
    @Override
    public Inscripcion findById(Long id) {
        log.info("Buscando inscripcion con id {}", id);
        return this.inscripcionRepository.findById(id)
                .orElseThrow(() -> new InscripcionException("Inscripcion con id '" + id + "' no encontrada"));
    }

    // lista todas las inscripciones de un torneo especifico
    @Transactional(readOnly = true)
    @Override
    public List<Inscripcion> findByTorneoId(Long torneoId) {
        log.info("Consultando inscripciones del torneo con id {}", torneoId);
        return this.inscripcionRepository.findByTorneoId(torneoId);
    }

    // lista todas las inscripciones de un equipo
    @Transactional(readOnly = true)
    @Override
    public List<Inscripcion> findByEquipoId(Long equipoId) {
        log.info("Consultando inscripciones del equipo con id {}", equipoId);
        return this.inscripcionRepository.findByEquipoId(equipoId);
    }

    // lista todas las inscripciones de un jugador individual
    @Transactional(readOnly = true)
    @Override
    public List<Inscripcion> findByJugadorId(Long jugadorId) {
        log.info("Consultando inscripciones del jugador con id {}", jugadorId);
        return this.inscripcionRepository.findByJugadorId(jugadorId);
    }

    // crea una nueva inscripcion validando todas las reglas de negocio
    @Transactional
    @Override
    public Inscripcion save(InscripcionDTO dto) {
        log.info("Intentando crear inscripcion para torneo id {}", dto.getTorneoId());

        // 1. validar que el torneo existe y esta abierto
        TorneoDTO torneo;
        try {
            torneo = this.torneoClient.getTorneoById(dto.getTorneoId());
        } catch (FeignException e) {
            log.error("Torneo con id {} no encontrado", dto.getTorneoId());
            throw new InscripcionException("El torneo con id '" + dto.getTorneoId() + "' no existe");
        }

        // 2. el torneo debe estar en estado ABIERTO
        if (!torneo.getEstado().equals("ABIERTO")) {
            log.warn("Intento de inscripcion a torneo no abierto, estado actual: {}", torneo.getEstado());
            throw new InscripcionException("El torneo no esta abierto para inscripciones. Estado actual: " + torneo.getEstado());
        }

        // 3. validar que no se supere el cupo maximo del torneo
        long inscritos = this.inscripcionRepository.countByTorneoIdAndEstado(dto.getTorneoId(), Inscripcion.Estado.CONFIRMADA);
        if (inscritos >= torneo.getCupoMaximo()) {
            log.warn("Torneo con id {} ha alcanzado el cupo maximo de {}", dto.getTorneoId(), torneo.getCupoMaximo());
            throw new InscripcionException("El torneo ha alcanzado el cupo maximo de " + torneo.getCupoMaximo() + " participantes");
        }

        // 4. validar plazo de inscripcion
        if (torneo.getFechaCierreInscripcion() != null && LocalDateTime.now().isAfter(torneo.getFechaCierreInscripcion())) {
            log.warn("Intento de inscripcion fuera de plazo en torneo id {}", dto.getTorneoId());
            throw new InscripcionException("El plazo de inscripcion para este torneo ha cerrado");
        }

        // 5. validaciones segun tipo de participante
        if (dto.getTipoParticipante() == Inscripcion.TipoParticipante.EQUIPO) {
            validarInscripcionEquipo(dto);
        } else {
            validarInscripcionIndividual(dto);
        }

        // 6. crear y guardar la inscripcion
        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setTorneoId(dto.getTorneoId());
        inscripcion.setEquipoId(dto.getEquipoId());
        inscripcion.setJugadorId(dto.getJugadorId());
        inscripcion.setTipoParticipante(dto.getTipoParticipante());
        inscripcion.setEstado(Inscripcion.Estado.CONFIRMADA);
        inscripcion.setFechaInscripcion(LocalDateTime.now());

        Inscripcion guardada = this.inscripcionRepository.save(inscripcion);
        log.info("Inscripcion creada exitosamente con id {}", guardada.getInscripcionId());
        return guardada;
    }

    // valida las reglas de negocio cuando el participante es un equipo
    private void validarInscripcionEquipo(InscripcionDTO dto) {
        if (dto.getEquipoId() == null) {
            throw new InscripcionException("Debe proporcionar el id del equipo para inscripcion de tipo EQUIPO");
        }

        // verificar que el equipo existe y esta activo en team-service
        EquipoDTO equipo;
        try {
            equipo = this.equipoClient.getEquipoById(dto.getEquipoId());
        } catch (FeignException e) {
            log.error("Equipo con id {} no encontrado", dto.getEquipoId());
            throw new InscripcionException("El equipo con id '" + dto.getEquipoId() + "' no existe");
        }

        if (!equipo.getEstado().equals("ACTIVO")) {
            log.warn("Equipo con id {} esta inactivo", dto.getEquipoId());
            throw new InscripcionException("El equipo '" + equipo.getNombre() + "' esta inactivo y no puede inscribirse");
        }

        // verificar que el equipo no tenga sancion activa
        Map<String, Boolean> respuestaEquipo = this.sancionClient.verificarSancion(null, dto.getEquipoId());
        boolean sancionado = Boolean.TRUE.equals(respuestaEquipo.get("sancionado"));
        if (sancionado) {
            log.warn("Equipo con id {} tiene sancion activa, inscripcion bloqueada", dto.getEquipoId());
            throw new InscripcionException("El equipo tiene una sancion activa y no puede inscribirse");
        }

        // verificar que el equipo no este ya inscrito en este torneo
        this.inscripcionRepository.findByTorneoIdAndEquipoId(dto.getTorneoId(), dto.getEquipoId())
                .ifPresent(i -> {
                    throw new InscripcionException("El equipo ya esta inscrito en este torneo");
                });
    }

    // valida las reglas de negocio cuando el participante es un jugador individual
    private void validarInscripcionIndividual(InscripcionDTO dto) {
        if (dto.getJugadorId() == null) {
            throw new InscripcionException("Debe proporcionar el id del jugador para inscripcion de tipo INDIVIDUAL");
        }

        // verificar que el jugador existe y esta activo en user-service
        UsuarioDTO usuario;
        try {
            usuario = this.usuarioClient.getUsuarioById(dto.getJugadorId());
        } catch (FeignException e) {
            log.error("Jugador con id {} no encontrado", dto.getJugadorId());
            throw new InscripcionException("El jugador con id '" + dto.getJugadorId() + "' no existe");
        }

        if (!usuario.getEstado().equals("ACTIVO")) {
            log.warn("Jugador con id {} esta inactivo o sancionado", dto.getJugadorId());
            throw new InscripcionException("El jugador '" + usuario.getNickname() + "' esta inactivo o sancionado y no puede inscribirse");
        }

        // verificar que el jugador no tenga sancion activa en sanction-service
        Map<String, Boolean> respuestaJugador = this.sancionClient.verificarSancion(dto.getJugadorId(), null);
        boolean sancionado = Boolean.TRUE.equals(respuestaJugador.get("sancionado"));
        if (sancionado) {
            log.warn("Jugador con id {} tiene sancion activa, inscripcion bloqueada", dto.getJugadorId());
            throw new InscripcionException("El jugador tiene una sancion activa y no puede inscribirse");
        }

        // verificar que el jugador no este ya inscrito en este torneo
        this.inscripcionRepository.findByTorneoIdAndJugadorId(dto.getTorneoId(), dto.getJugadorId())
                .ifPresent(i -> {
                    throw new InscripcionException("El jugador ya esta inscrito en este torneo");
                });
    }

    // actualiza el estado de una inscripcion existente
    @Transactional
    @Override
    public Inscripcion actualizarEstado(Long id, String nuevoEstado) {
        log.info("Actualizando estado de inscripcion id {} a {}", id, nuevoEstado);
        return this.inscripcionRepository.findById(id).map(inscripcion -> {
            // no se puede cambiar estado de una inscripcion ya cancelada
            if (inscripcion.getEstado() == Inscripcion.Estado.CANCELADA) {
                throw new InscripcionException("No se puede cambiar el estado de una inscripcion cancelada");
            }
            inscripcion.setEstado(Inscripcion.Estado.valueOf(nuevoEstado.toUpperCase()));
            return this.inscripcionRepository.save(inscripcion);
        }).orElseThrow(() -> new InscripcionException("Inscripcion con id '" + id + "' no encontrada"));
    }

    // cancela una inscripcion
    @Transactional
    @Override
    public Inscripcion cancelar(Long id) {
        log.info("Cancelando inscripcion con id {}", id);
        return this.inscripcionRepository.findById(id).map(inscripcion -> {
            if (inscripcion.getEstado() == Inscripcion.Estado.CANCELADA) {
                throw new InscripcionException("La inscripcion ya esta cancelada");
            }
            inscripcion.setEstado(Inscripcion.Estado.CANCELADA);
            return this.inscripcionRepository.save(inscripcion);
        }).orElseThrow(() -> new InscripcionException("Inscripcion con id '" + id + "' no encontrada"));
    }
}
