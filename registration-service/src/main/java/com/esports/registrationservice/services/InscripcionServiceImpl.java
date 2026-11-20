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

    private static final Logger log = LoggerFactory.getLogger(InscripcionServiceImpl.class);

    @Autowired
    private InscripcionRepository inscripcionRepository;

    @Autowired
    private TorneoClient torneoClient;

    @Autowired private CupoService cupos;
    @Autowired private com.esports.registrationservice.clients.JuegoClient juegoClient;

    @Autowired
    private UsuarioClient usuarioClient;

    @Autowired
    private EquipoClient equipoClient;

    @Autowired
    private SancionClient sancionClient;

    @Transactional(readOnly = true)
    @Override
    public List<Inscripcion> findAll() {
        log.info("Consultando todas las inscripciones");
        return this.inscripcionRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public Inscripcion findById(Long id) {
        log.info("Buscando inscripcion con id {}", id);
        return this.inscripcionRepository.findById(id)
                .orElseThrow(() -> new InscripcionException("Inscripcion con id '" + id + "' no encontrada",404));
    }

    @Transactional(readOnly = true)
    @Override
    public List<Inscripcion> findByTorneoId(Long torneoId) {
        log.info("Consultando inscripciones del torneo con id {}", torneoId);
        return this.inscripcionRepository.findByTorneoId(torneoId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Inscripcion> findByEquipoId(Long equipoId) {
        log.info("Consultando inscripciones del equipo con id {}", equipoId);
        return this.inscripcionRepository.findByEquipoId(equipoId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Inscripcion> findByJugadorId(Long jugadorId) {
        log.info("Consultando inscripciones del jugador con id {}", jugadorId);
        return this.inscripcionRepository.findByJugadorId(jugadorId);
    }

    @Transactional
    @Override
    public Inscripcion save(InscripcionDTO dto) {
        log.info("Intentando crear inscripcion para torneo id {}", dto.getTorneoId());

        try { cupos.ensure(dto.getTorneoId()); } catch(org.springframework.dao.DataIntegrityViolationException ignored) {}
        cupos.lock(dto.getTorneoId());
        TorneoDTO torneo;
        try {
            torneo = this.torneoClient.getTorneoById(dto.getTorneoId());
        } catch (FeignException e) {
            log.error("Torneo con id {} no encontrado", dto.getTorneoId());
            throw new InscripcionException("El torneo con id '" + dto.getTorneoId() + "' no existe");
        }

        if (!torneo.getEstado().equals("ABIERTO")) {
            log.warn("Intento de inscripcion a torneo no abierto, estado actual: {}", torneo.getEstado());
            throw new InscripcionException("El torneo no esta abierto para inscripciones. Estado actual: " + torneo.getEstado());
        }

        long inscritos = ocupados(dto.getTorneoId());
        boolean individual=torneo.getModalidad()!=null && torneo.getModalidad().toLowerCase().matches(".*(solo|individual|1\\s*(vs|v)\\s*1).*");
        if(individual != (dto.getTipoParticipante()==Inscripcion.TipoParticipante.INDIVIDUAL)) throw new InscripcionException("El tipo de participante no coincide con la modalidad del torneo");
        if(dto.getTipoParticipante()==Inscripcion.TipoParticipante.EQUIPO && dto.getJugadorId()!=null || dto.getTipoParticipante()==Inscripcion.TipoParticipante.INDIVIDUAL && dto.getEquipoId()!=null) throw new InscripcionException("Indica exclusivamente equipo o jugador");
        if (inscritos >= torneo.getCupoMaximo()) {
            log.warn("Torneo con id {} ha alcanzado el cupo maximo de {}", dto.getTorneoId(), torneo.getCupoMaximo());
            throw new InscripcionException("El torneo ha alcanzado el cupo maximo de " + torneo.getCupoMaximo() + " participantes");
        }

        if (torneo.getFechaCierreInscripcion() != null && com.esports.common.ArenaTime.now().isAfter(torneo.getFechaCierreInscripcion())) {
            log.warn("Intento de inscripcion fuera de plazo en torneo id {}", dto.getTorneoId());
            throw new InscripcionException("El plazo de inscripcion para este torneo ha cerrado");
        }

        if (dto.getTipoParticipante() == Inscripcion.TipoParticipante.EQUIPO) {
            validarInscripcionEquipo(dto);
            var equipo=equipoClient.getEquipoById(dto.getEquipoId());
            var juego=juegoClient.getJuegoById(torneo.getJuegoId());
            if(!Boolean.TRUE.equals(juego.estado()) || !java.util.Objects.equals(equipo.getJuegoPrincipalId(),torneo.getJuegoId())) throw new InscripcionException("El equipo y el torneo deben tener el mismo juego habilitado");
            var miembros=equipoClient.miembros(dto.getEquipoId());
            if(miembros.stream().map(com.esports.registrationservice.clients.EquipoClient.Miembro::usuarioId).distinct().count()<juego.jugadoresPorEquipo()) throw new InscripcionException("El equipo no tiene suficientes integrantes");
        } else {
            validarInscripcionIndividual(dto);
        }

        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setTorneoId(dto.getTorneoId());
        inscripcion.setEquipoId(dto.getEquipoId());
        inscripcion.setJugadorId(dto.getJugadorId());
        inscripcion.setTipoParticipante(dto.getTipoParticipante());
        inscripcion.setEstado(Inscripcion.Estado.PENDIENTE);
        inscripcion.setFechaInscripcion(com.esports.common.ArenaTime.now());

        Inscripcion guardada = this.inscripcionRepository.save(inscripcion);
        log.info("Inscripcion creada exitosamente con id {}", guardada.getInscripcionId());
        return guardada;
    }

    public long ocupados(Long torneoId) {
        return inscripcionRepository.countByTorneoIdAndEstado(torneoId,Inscripcion.Estado.CONFIRMADA)+inscripcionRepository.countByTorneoIdAndEstado(torneoId,Inscripcion.Estado.PENDIENTE);
    }
    private void validarInscripcionEquipo(InscripcionDTO dto) {
        if (dto.getEquipoId() == null) {
            throw new InscripcionException("Debe proporcionar el id del equipo para inscripcion de tipo EQUIPO");
        }

        EquipoDTO equipo;
        try {
            equipo = this.equipoClient.getEquipoById(dto.getEquipoId());
        } catch (FeignException e) {
            log.error("Equipo con id {} no encontrado", dto.getEquipoId());
            throw new InscripcionException("El equipo con id '" + dto.getEquipoId() + "' no existe");
        }

        if (!equipo.getEstado()) {
            log.warn("Equipo con id {} esta inactivo", dto.getEquipoId());
            throw new InscripcionException("El equipo '" + equipo.getNombre() + "' esta inactivo y no puede inscribirse");
        }

        Map<String, Boolean> respuestaEquipo = this.sancionClient.verificarSancion(null, dto.getEquipoId());
        boolean sancionado = Boolean.TRUE.equals(respuestaEquipo.get("sancionado"));
        if (sancionado) {
            log.warn("Equipo con id {} tiene sancion activa, inscripcion bloqueada", dto.getEquipoId());
            throw new InscripcionException("El equipo tiene una sancion activa y no puede inscribirse");
        }

        this.inscripcionRepository.findByTorneoIdAndEquipoId(dto.getTorneoId(), dto.getEquipoId())
                .ifPresent(i -> {
                    throw new InscripcionException("El equipo ya esta inscrito en este torneo");
                });
    }

    private void validarInscripcionIndividual(InscripcionDTO dto) {
        if (dto.getJugadorId() == null) {
            throw new InscripcionException("Debe proporcionar el id del jugador para inscripcion de tipo INDIVIDUAL");
        }

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

        Map<String, Boolean> respuestaJugador = this.sancionClient.verificarSancion(dto.getJugadorId(), null);
        boolean sancionado = Boolean.TRUE.equals(respuestaJugador.get("sancionado"));
        if (sancionado) {
            log.warn("Jugador con id {} tiene sancion activa, inscripcion bloqueada", dto.getJugadorId());
            throw new InscripcionException("El jugador tiene una sancion activa y no puede inscribirse");
        }

        this.inscripcionRepository.findByTorneoIdAndJugadorId(dto.getTorneoId(), dto.getJugadorId())
                .ifPresent(i -> {
                    throw new InscripcionException("El jugador ya esta inscrito en este torneo");
                });
    }

    @Transactional
    @Override
    public Inscripcion actualizarEstado(Long id, String nuevoEstado) {
        log.info("Actualizando estado de inscripcion id {} a {}", id, nuevoEstado);
        return this.inscripcionRepository.findById(id).map(inscripcion -> {
            var siguiente=Inscripcion.Estado.valueOf(nuevoEstado.toUpperCase());
            if(inscripcion.getEstado()!=Inscripcion.Estado.PENDIENTE || !java.util.Set.of(Inscripcion.Estado.CONFIRMADA,Inscripcion.Estado.RECHAZADA,Inscripcion.Estado.CANCELADA).contains(siguiente)) throw new InscripcionException("Transicion de inscripcion no permitida");
            cupos.lock(inscripcion.getTorneoId());
            inscripcion.setEstado(siguiente);
            return this.inscripcionRepository.save(inscripcion);
        }).orElseThrow(() -> new InscripcionException("Inscripcion con id '" + id + "' no encontrada",404));
    }

    @Transactional
    @Override
    public Inscripcion cancelar(Long id) {
        log.info("Cancelando inscripcion con id {}", id);
        return this.inscripcionRepository.findById(id).map(inscripcion -> {
            if (inscripcion.getEstado() == Inscripcion.Estado.CANCELADA) {
                throw new InscripcionException("La inscripcion ya esta cancelada");
            }
            cupos.lock(inscripcion.getTorneoId());
            inscripcion.setEstado(Inscripcion.Estado.CANCELADA);
            return this.inscripcionRepository.save(inscripcion);
        }).orElseThrow(() -> new InscripcionException("Inscripcion con id '" + id + "' no encontrada",404));
    }
}