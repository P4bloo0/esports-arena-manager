package com.esports.rankingservice.services;

import com.esports.rankingservice.clients.PartidaClient;
import com.esports.rankingservice.clients.ResultadoClient;
import com.esports.rankingservice.exceptions.RankingException;
import com.esports.rankingservice.models.Ranking;
import com.esports.rankingservice.models.dtos.ActualizarPuntosDTO;
import com.esports.rankingservice.models.dtos.PartidaDTO;
import com.esports.rankingservice.models.dtos.RankingDTO;
import com.esports.rankingservice.models.dtos.ResultadoDTO;
import com.esports.rankingservice.repositories.RankingRepository;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class RankingServiceImpl implements RankingService {

    private static final Logger log = LoggerFactory.getLogger(RankingServiceImpl.class);

    @Autowired
    private RankingRepository rankingRepository;

    @Autowired
    private ResultadoClient resultadoClient;

    @Autowired
    private PartidaClient partidaClient;


    @Transactional(readOnly = true)
    @Override
    public List<Ranking> findAll() {
        log.info("Consultando todos los registros de ranking");
        return this.rankingRepository.findAll();
    }


    @Transactional(readOnly = true)
    @Override
    public Ranking findById(Long id) {
        log.info("Buscando ranking con id {}", id);
        return this.rankingRepository.findById(id)
                .orElseThrow(() -> new RankingException("Ranking con id '" + id + "' no encontrado"));
    }


    @Transactional(readOnly = true)
    @Override
    public List<Ranking> findByTorneoId(Long torneoId) {
        log.info("Consultando ranking del torneo con id {}", torneoId);
        return this.rankingRepository.findByTorneoIdOrderByPuntosDescDiferenciaDesc(torneoId);
    }


    @Transactional(readOnly = true)
    @Override
    public Ranking findByTorneoIdAndParticipanteId(Long torneoId, Long participanteId) {
        log.info("Buscando posicion del participante {} en torneo {}", participanteId, torneoId);
        return this.rankingRepository.findByTorneoIdAndParticipanteId(torneoId, participanteId)
                .orElseThrow(() -> new RankingException("No existe ranking para el participante " + participanteId + " en el torneo " + torneoId));
    }


    @Transactional
    @Override
    public Ranking save(RankingDTO dto) {
        log.info("Creando registro de ranking para participante {} en torneo {}", dto.getParticipanteId(), dto.getTorneoId());


        this.rankingRepository.findByTorneoIdAndParticipanteId(dto.getTorneoId(), dto.getParticipanteId())
                .ifPresent(r -> {
                    throw new RankingException("El participante ya tiene un registro en el ranking de este torneo");
                });

        Ranking ranking = new Ranking();
        ranking.setTorneoId(dto.getTorneoId());
        ranking.setParticipanteId(dto.getParticipanteId());
        ranking.setPuntos(0);
        ranking.setVictorias(0);
        ranking.setDerrotas(0);
        ranking.setDiferencia(0);
        ranking.setPosicion(0);
        ranking.setEstado(Ranking.Estado.ACTIVO);

        Ranking guardado = this.rankingRepository.save(ranking);
        log.info("Registro de ranking creado con id {}", guardado.getRankingId());
        return guardado;
    }


    @Transactional
    @Override
    public Ranking actualizarPuntos(Long id, ActualizarPuntosDTO dto) {
        log.info("Actualizando puntos del ranking con id {}", id);
        return this.rankingRepository.findById(id).map(ranking -> {


            if (ranking.getEstado() == Ranking.Estado.CERRADO) {
                throw new RankingException("No se puede actualizar un ranking cerrado");
            }

            ranking.setPuntos(dto.getPuntos());
            ranking.setVictorias(dto.getVictorias());
            ranking.setDerrotas(dto.getDerrotas());
            ranking.setDiferencia(dto.getDiferencia());

            Ranking actualizado = this.rankingRepository.save(ranking);

            // recalcular posiciones de todo el torneo despues de actualizar
            recalcularPosiciones(ranking.getTorneoId());

            return actualizado;
        }).orElseThrow(() -> new RankingException("Ranking con id '" + id + "' no encontrado"));
    }


    @Transactional
    @Override
    public List<Ranking> recalcularDesdeResultados(Long torneoId) {
        log.info("Recalculando ranking del torneo {} desde resultados validados", torneoId);


        List<ResultadoDTO> resultados;
        try {
            resultados = this.resultadoClient.getResultadosByTorneo(torneoId);
        } catch (FeignException e) {
            log.error("Error al obtener resultados del torneo {}", torneoId);
            throw new RankingException("No se pudo obtener los resultados del torneo con id '" + torneoId + "'");
        }


        List<ResultadoDTO> validados = resultados.stream()
                .filter(r -> "VALIDADO".equals(r.getEstadoValidacion()))
                .toList();

        log.info("Se encontraron {} resultados validados para el torneo {}", validados.size(), torneoId);


        for (ResultadoDTO resultado : validados) {
            PartidaDTO partida;
            try {
                partida = this.partidaClient.getPartidaById(resultado.getPartidaId());
            } catch (FeignException e) {
                log.warn("No se pudo obtener la partida {} al recalcular ranking", resultado.getPartidaId());
                continue;
            }

            Long ganadorId = resultado.getGanadorId();
            Long perdedorId = partida.getParticipanteAId().equals(ganadorId)
                    ? partida.getParticipanteBId()
                    : partida.getParticipanteAId();

            int difGanador = resultado.getPuntajeA() - resultado.getPuntajeB();
            int difPerdedor = resultado.getPuntajeB() - resultado.getPuntajeA();


            this.rankingRepository.findByTorneoIdAndParticipanteId(torneoId, ganadorId).ifPresent(r -> {
                r.setPuntos(r.getPuntos() + 3);
                r.setVictorias(r.getVictorias() + 1);
                r.setDiferencia(r.getDiferencia() + difGanador);
                this.rankingRepository.save(r);
            });


            this.rankingRepository.findByTorneoIdAndParticipanteId(torneoId, perdedorId).ifPresent(r -> {
                r.setDerrotas(r.getDerrotas() + 1);
                r.setDiferencia(r.getDiferencia() + difPerdedor);
                this.rankingRepository.save(r);
            });
        }


        recalcularPosiciones(torneoId);

        log.info("Recalculo de ranking completado para torneo {}", torneoId);
        return this.rankingRepository.findByTorneoIdOrderByPuntosDescDiferenciaDesc(torneoId);
    }


    @Transactional
    @Override
    public List<Ranking> cerrarRanking(Long torneoId) {
        log.info("Cerrando ranking del torneo con id {}", torneoId);

        List<Ranking> rankings = this.rankingRepository.findByTorneoIdOrderByPuntosDesc(torneoId);

        if (rankings.isEmpty()) {
            throw new RankingException("No existe ranking para el torneo con id '" + torneoId + "'");
        }

        rankings.forEach(r -> r.setEstado(Ranking.Estado.CERRADO));
        List<Ranking> cerrados = this.rankingRepository.saveAll(rankings);
        log.info("Ranking del torneo {} cerrado exitosamente", torneoId);
        return cerrados;
    }


    private void recalcularPosiciones(Long torneoId) {
        List<Ranking> rankings = this.rankingRepository.findByTorneoIdOrderByPuntosDesc(torneoId);
        AtomicInteger posicion = new AtomicInteger(1);
        rankings.forEach(r -> {
            r.setPosicion(posicion.getAndIncrement());
            this.rankingRepository.save(r);
        });
        log.info("Posiciones recalculadas para torneo {}", torneoId);
    }
}
