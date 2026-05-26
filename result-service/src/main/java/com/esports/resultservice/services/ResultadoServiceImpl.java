package com.esports.resultservice.services;

import com.esports.resultservice.clients.PartidaClient;
import com.esports.resultservice.exceptions.ResultadoException;
import com.esports.resultservice.models.Resultado;
import com.esports.resultservice.models.dtos.AnulacionDTO;
import com.esports.resultservice.models.dtos.PartidaDTO;
import com.esports.resultservice.models.dtos.ResultadoDTO;
import com.esports.resultservice.repositories.ResultadoRepository;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ResultadoServiceImpl implements ResultadoService {

    private static final Logger log = LoggerFactory.getLogger(ResultadoServiceImpl.class);

    @Autowired
    private ResultadoRepository resultadoRepository;

    @Autowired
    private PartidaClient partidaClient;

    // devuelve todos los resultados registrados
    @Transactional(readOnly = true)
    @Override
    public List<Resultado> findAll() {
        log.info("Consultando todos los resultados");
        return this.resultadoRepository.findAll();
    }

    // busca un resultado por su id
    @Transactional(readOnly = true)
    @Override
    public Resultado findById(Long id) {
        log.info("Buscando resultado con id {}", id);
        return this.resultadoRepository.findById(id)
                .orElseThrow(() -> new ResultadoException("Resultado con id '" + id + "' no encontrado"));
    }

    // busca el resultado de una partida especifica
    @Transactional(readOnly = true)
    @Override
    public Resultado findByPartidaId(Long partidaId) {
        log.info("Buscando resultado de la partida con id {}", partidaId);
        return this.resultadoRepository.findByPartidaId(partidaId)
                .orElseThrow(() -> new ResultadoException("No existe resultado para la partida con id '" + partidaId + "'"));
    }

    // lista resultados de todas las partidas de un torneo
    @Transactional(readOnly = true)
    @Override
    public List<Resultado> findByTorneoId(Long torneoId) {
        log.info("Consultando resultados del torneo con id {}", torneoId);

        // primero obtiene las partidas del torneo desde match-service
        List<PartidaDTO> partidas;
        try {
            partidas = this.partidaClient.getPartidasByTorneo(torneoId);
        } catch (FeignException e) {
            log.error("Error al consultar partidas del torneo {}", torneoId);
            throw new ResultadoException("No se pudo obtener las partidas del torneo con id '" + torneoId + "'");
        }

        List<Long> partidaIds = partidas.stream().map(PartidaDTO::getPartidaId).toList();
        return this.resultadoRepository.findByPartidaIdIn(partidaIds);
    }

    // registra un nuevo resultado para una partida
    @Transactional
    @Override
    public Resultado save(ResultadoDTO dto) {
        log.info("Intentando registrar resultado para partida id {}", dto.getPartidaId());

        // 1. validar que la partida existe en match-service
        PartidaDTO partida;
        try {
            partida = this.partidaClient.getPartidaById(dto.getPartidaId());
        } catch (FeignException e) {
            log.error("Partida con id {} no encontrada", dto.getPartidaId());
            throw new ResultadoException("La partida con id '" + dto.getPartidaId() + "' no existe");
        }

        // 2. la partida debe estar EN_CURSO o FINALIZADA para registrar resultado
        if (partida.getEstado().equals("CANCELADA")) {
            log.warn("Intento de registrar resultado en partida cancelada id {}", dto.getPartidaId());
            throw new ResultadoException("No se puede registrar resultado de una partida cancelada");
        }

        if (partida.getEstado().equals("PROGRAMADA")) {
            log.warn("Intento de registrar resultado en partida aun no iniciada id {}", dto.getPartidaId());
            throw new ResultadoException("No se puede registrar resultado de una partida que aun no ha comenzado");
        }

        // 3. no puede existir ya un resultado para esta partida
        this.resultadoRepository.findByPartidaId(dto.getPartidaId()).ifPresent(r -> {
            throw new ResultadoException("Ya existe un resultado registrado para la partida con id '" + dto.getPartidaId() + "'");
        });

        // 4. el ganador debe ser uno de los dos participantes de la partida
        if (!dto.getGanadorId().equals(partida.getParticipanteAId()) &&
            !dto.getGanadorId().equals(partida.getParticipanteBId())) {
            log.warn("Ganador id {} no corresponde a ningun participante de la partida {}", dto.getGanadorId(), dto.getPartidaId());
            throw new ResultadoException("El ganador indicado no es participante de esta partida");
        }

        // 5. crear y guardar el resultado
        Resultado resultado = new Resultado();
        resultado.setPartidaId(dto.getPartidaId());
        resultado.setGanadorId(dto.getGanadorId());
        resultado.setPuntajeA(dto.getPuntajeA());
        resultado.setPuntajeB(dto.getPuntajeB());
        resultado.setEstadoValidacion(Resultado.EstadoValidacion.PENDIENTE);
        resultado.setFechaRegistro(LocalDateTime.now());

        Resultado guardado = this.resultadoRepository.save(resultado);
        log.info("Resultado registrado exitosamente con id {}", guardado.getResultadoId());
        return guardado;
    }

    // actualiza un resultado solo si aun no ha sido validado
    @Transactional
    @Override
    public Resultado update(Long id, ResultadoDTO dto) {
        log.info("Actualizando resultado con id {}", id);
        return this.resultadoRepository.findById(id).map(resultado -> {

            // resultado validado no puede modificarse
            if (resultado.getEstadoValidacion() == Resultado.EstadoValidacion.VALIDADO) {
                throw new ResultadoException("No se puede modificar un resultado ya validado");
            }

            if (resultado.getEstadoValidacion() == Resultado.EstadoValidacion.ANULADO) {
                throw new ResultadoException("No se puede modificar un resultado anulado");
            }

            resultado.setGanadorId(dto.getGanadorId());
            resultado.setPuntajeA(dto.getPuntajeA());
            resultado.setPuntajeB(dto.getPuntajeB());
            return this.resultadoRepository.save(resultado);
        }).orElseThrow(() -> new ResultadoException("Resultado con id '" + id + "' no encontrado"));
    }

    // valida un resultado pendiente (solo organizador deberia poder hacer esto)
    @Transactional
    @Override
    public Resultado validar(Long id) {
        log.info("Validando resultado con id {}", id);
        return this.resultadoRepository.findById(id).map(resultado -> {

            if (resultado.getEstadoValidacion() != Resultado.EstadoValidacion.PENDIENTE) {
                throw new ResultadoException("Solo se pueden validar resultados en estado PENDIENTE. Estado actual: " + resultado.getEstadoValidacion());
            }

            resultado.setEstadoValidacion(Resultado.EstadoValidacion.VALIDADO);
            Resultado validado = this.resultadoRepository.save(resultado);
            log.info("Resultado con id {} validado exitosamente", id);
            return validado;
        }).orElseThrow(() -> new ResultadoException("Resultado con id '" + id + "' no encontrado"));
    }

    // anula un resultado con una justificacion obligatoria
    @Transactional
    @Override
    public Resultado anular(Long id, AnulacionDTO dto) {
        log.info("Anulando resultado con id {}", id);
        return this.resultadoRepository.findById(id).map(resultado -> {

            if (resultado.getEstadoValidacion() == Resultado.EstadoValidacion.ANULADO) {
                throw new ResultadoException("El resultado ya esta anulado");
            }

            resultado.setEstadoValidacion(Resultado.EstadoValidacion.ANULADO);
            resultado.setJustificacionAnulacion(dto.getJustificacion());
            Resultado anulado = this.resultadoRepository.save(resultado);
            log.warn("Resultado con id {} anulado. Motivo: {}", id, dto.getJustificacion());
            return anulado;
        }).orElseThrow(() -> new ResultadoException("Resultado con id '" + id + "' no encontrado"));
    }
}
