package com.esports.matchservice.services;

import com.esports.matchservice.clients.InscripcionClient;
import com.esports.matchservice.clients.TorneoClient;
import com.esports.matchservice.exceptions.PartidaException;
import com.esports.matchservice.models.Partida;
import com.esports.matchservice.models.dtos.InscripcionDTO;
import com.esports.matchservice.models.dtos.PartidaDTO;
import com.esports.matchservice.models.dtos.TorneoDTO;
import com.esports.matchservice.repositories.PartidaRepository;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class PartidaServiceImpl implements PartidaService {

    // log para trazabilidad de operaciones importantes
    private static final Logger log = LoggerFactory.getLogger(PartidaServiceImpl.class);

    @Autowired
    private PartidaRepository partidaRepository;

    @Autowired
    private TorneoClient torneoClient;

    @Autowired
    private InscripcionClient inscripcionClient;

    // devuelve todas las partidas registradas
    @Transactional(readOnly = true)
    @Override
    public List<Partida> findAll() {
        log.info("Consultando todas las partidas");
        return this.partidaRepository.findAll();
    }

    // busca una partida por su id, lanza excepcion si no existe
    @Transactional(readOnly = true)
    @Override
    public Partida findById(Long id) {
        log.info("Buscando partida con id {}", id);
        return this.partidaRepository.findById(id)
                .orElseThrow(() -> new PartidaException("Partida con id '" + id + "' no encontrada"));
    }

    // lista todas las partidas de un torneo
    @Transactional(readOnly = true)
    @Override
    public List<Partida> findByTorneoId(Long torneoId) {
        log.info("Consultando partidas del torneo con id {}", torneoId);
        return this.partidaRepository.findByTorneoId(torneoId);
    }

    // lista partidas de un torneo filtradas por ronda
    @Transactional(readOnly = true)
    @Override
    public List<Partida> findByTorneoIdAndRonda(Long torneoId, String ronda) {
        log.info("Consultando partidas del torneo {} en ronda {}", torneoId, ronda);
        return this.partidaRepository.findByTorneoIdAndRonda(torneoId, ronda);
    }

    // lista partidas por estado
    @Transactional(readOnly = true)
    @Override
    public List<Partida> findByEstado(String estado) {
        log.info("Consultando partidas con estado {}", estado);
        return this.partidaRepository.findByEstado(Partida.Estado.valueOf(estado.toUpperCase()));
    }

    // crea una nueva partida validando todas las reglas de negocio
    @Transactional
    @Override
    public Partida save(PartidaDTO dto) {
        log.info("Intentando crear partida en torneo id {}", dto.getTorneoId());

        // 1. validar que el torneo existe
        TorneoDTO torneo;
        try {
            torneo = this.torneoClient.getTorneoById(dto.getTorneoId());
        } catch (FeignException e) {
            log.error("Torneo con id {} no encontrado al crear partida", dto.getTorneoId());
            throw new PartidaException("El torneo con id '" + dto.getTorneoId() + "' no existe");
        }

        // 2. el torneo debe estar EN_CURSO para poder crear partidas
        if (!torneo.getEstado().equals("EN_CURSO")) {
            log.warn("Intento de crear partida en torneo con estado {}", torneo.getEstado());
            throw new PartidaException("Solo se pueden crear partidas en torneos EN_CURSO. Estado actual: " + torneo.getEstado());
        }

        // 3. los dos participantes no pueden ser el mismo
        if (dto.getParticipanteAId().equals(dto.getParticipanteBId())) {
            throw new PartidaException("Los dos participantes no pueden ser el mismo");
        }

        // 4. validar que ambos participantes esten inscritos en el torneo
        List<InscripcionDTO> inscripciones;
        try {
            inscripciones = this.inscripcionClient.getInscripcionesByTorneo(dto.getTorneoId());
        } catch (FeignException e) {
            log.error("Error al consultar inscripciones del torneo {}", dto.getTorneoId());
            throw new PartidaException("No se pudo verificar las inscripciones del torneo");
        }

        boolean aInscrito = inscripciones.stream().anyMatch(i ->
                (i.getEquipoId() != null && i.getEquipoId().equals(dto.getParticipanteAId())) ||
                (i.getJugadorId() != null && i.getJugadorId().equals(dto.getParticipanteAId())));

        boolean bInscrito = inscripciones.stream().anyMatch(i ->
                (i.getEquipoId() != null && i.getEquipoId().equals(dto.getParticipanteBId())) ||
                (i.getJugadorId() != null && i.getJugadorId().equals(dto.getParticipanteBId())));

        if (!aInscrito) {
            log.warn("Participante A con id {} no esta inscrito en el torneo {}", dto.getParticipanteAId(), dto.getTorneoId());
            throw new PartidaException("El participante A con id '" + dto.getParticipanteAId() + "' no esta inscrito en este torneo");
        }

        if (!bInscrito) {
            log.warn("Participante B con id {} no esta inscrito en el torneo {}", dto.getParticipanteBId(), dto.getTorneoId());
            throw new PartidaException("El participante B con id '" + dto.getParticipanteBId() + "' no esta inscrito en este torneo");
        }

        // 5. no duplicar el mismo enfrentamiento en la misma ronda
        this.partidaRepository.findByTorneoIdAndParticipanteAIdAndParticipanteBIdAndRonda(
                dto.getTorneoId(), dto.getParticipanteAId(), dto.getParticipanteBId(), dto.getRonda()
        ).ifPresent(p -> {
            throw new PartidaException("Ya existe una partida entre estos participantes en la ronda '" + dto.getRonda() + "'");
        });

        // 6. crear y guardar la partida
        Partida partida = new Partida();
        partida.setTorneoId(dto.getTorneoId());
        partida.setParticipanteAId(dto.getParticipanteAId());
        partida.setParticipanteBId(dto.getParticipanteBId());
        partida.setRonda(dto.getRonda());
        partida.setFechaHora(dto.getFechaHora());
        partida.setEstado(Partida.Estado.PROGRAMADA);

        Partida guardada = this.partidaRepository.save(partida);
        log.info("Partida creada exitosamente con id {}", guardada.getPartidaId());
        return guardada;
    }

    // actualiza horario, participantes o ronda de una partida existente
    @Transactional
    @Override
    public Partida update(Long id, PartidaDTO dto) {
        log.info("Actualizando partida con id {}", id);
        return this.partidaRepository.findById(id).map(partida -> {

            // no se puede modificar una partida cancelada o finalizada
            if (partida.getEstado() == Partida.Estado.CANCELADA || partida.getEstado() == Partida.Estado.FINALIZADA) {
                throw new PartidaException("No se puede modificar una partida " + partida.getEstado());
            }

            partida.setRonda(dto.getRonda());
            partida.setFechaHora(dto.getFechaHora());
            partida.setParticipanteAId(dto.getParticipanteAId());
            partida.setParticipanteBId(dto.getParticipanteBId());
            return this.partidaRepository.save(partida);
        }).orElseThrow(() -> new PartidaException("Partida con id '" + id + "' no encontrada"));
    }

    // cancela una partida
    @Transactional
    @Override
    public Partida cancelar(Long id) {
        log.info("Cancelando partida con id {}", id);
        return this.partidaRepository.findById(id).map(partida -> {

            // no se puede cancelar una partida ya finalizada
            if (partida.getEstado() == Partida.Estado.FINALIZADA) {
                throw new PartidaException("No se puede cancelar una partida ya finalizada");
            }

            if (partida.getEstado() == Partida.Estado.CANCELADA) {
                throw new PartidaException("La partida ya esta cancelada");
            }

            partida.setEstado(Partida.Estado.CANCELADA);
            return this.partidaRepository.save(partida);
        }).orElseThrow(() -> new PartidaException("Partida con id '" + id + "' no encontrada"));
    }
}
