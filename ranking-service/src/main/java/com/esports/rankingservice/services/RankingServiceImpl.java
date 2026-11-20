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
    @Autowired private com.esports.rankingservice.clients.InscripcionesClient inscripciones;
    @Autowired private RankingControlService control;


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
                .orElseThrow(() -> new RankingException("Ranking con id '" + id + "' no encontrado",404));
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
        }).orElseThrow(() -> new RankingException("Ranking con id '" + id + "' no encontrado",404));
    }


    @Transactional
    @Override
    public List<Ranking> recalcularDesdeResultados(Long torneoId) {
        try { control.ensure(torneoId); } catch(org.springframework.dao.DataIntegrityViolationException ignored) {}
        control.lock(torneoId);
        var existentes=rankingRepository.findByTorneoIdOrderByPuntosDescDiferenciaDesc(torneoId);
        if(existentes.stream().anyMatch(r -> r.getEstado()==Ranking.Estado.CERRADO)) throw new RankingException("El ranking esta cerrado");
        java.util.Map<Long,Ranking> tabla=new java.util.LinkedHashMap<>();
        for(var r:existentes) { r.setPuntos(0); r.setVictorias(0); r.setDerrotas(0); r.setDiferencia(0); tabla.put(r.getParticipanteId(),r); }
        for(var i:inscripciones.listar(torneoId)) if("CONFIRMADA".equals(i.estado())) participante(tabla,torneoId,i.equipoId()!=null?i.equipoId():i.jugadorId());
        List<ResultadoDTO> resultados;
        try { resultados=resultadoClient.getResultadosByTorneo(torneoId); } catch(FeignException ex) { throw new RankingException("No se pudieron consultar los resultados"); }
        for(var resultado:resultados) {
            if(!"VALIDADO".equals(resultado.getEstadoValidacion())) continue;
            var partida=partidaClient.getPartidaById(resultado.getPartidaId());
            var a=participante(tabla,torneoId,partida.getParticipanteAId());
            var b=participante(tabla,torneoId,partida.getParticipanteBId());
            if(!resultado.getGanadorId().equals(a.getParticipanteId()) && !resultado.getGanadorId().equals(b.getParticipanteId())) throw new RankingException("El ganador no pertenece a la partida");
            int diferencia=resultado.getPuntajeA()-resultado.getPuntajeB();
            a.setDiferencia(a.getDiferencia()+diferencia); b.setDiferencia(b.getDiferencia()-diferencia);
            var ganador=resultado.getGanadorId().equals(a.getParticipanteId())?a:b;
            var perdedor=ganador==a?b:a;
            ganador.setPuntos(ganador.getPuntos()+3); ganador.setVictorias(ganador.getVictorias()+1); perdedor.setDerrotas(perdedor.getDerrotas()+1);
        }
        var lista=tabla.values().stream().sorted(java.util.Comparator.comparing(Ranking::getPuntos).reversed().thenComparing(java.util.Comparator.comparing(Ranking::getDiferencia).reversed()).thenComparing(Ranking::getParticipanteId)).toList();
        int posicion=1; for(var r:lista) { r.setPosicion(posicion++); rankingRepository.save(r); }
        return lista;
    }
    private Ranking participante(java.util.Map<Long,Ranking> tabla,Long torneo,Long id) {
        return tabla.computeIfAbsent(id,k -> { var r=new Ranking(); r.setTorneoId(torneo); r.setParticipanteId(k); r.setPuntos(0); r.setVictorias(0); r.setDerrotas(0); r.setDiferencia(0); r.setPosicion(0); r.setEstado(Ranking.Estado.ACTIVO); return r; });
    }

    @Transactional
    @Override
    public List<Ranking> cerrarRanking(Long torneoId) {
        log.info("Cerrando ranking del torneo con id {}", torneoId);

        List<Ranking> rankings = this.rankingRepository.findByTorneoIdOrderByPuntosDescDiferenciaDesc(torneoId);

        if (rankings.isEmpty()) {
            throw new RankingException("No existe ranking para el torneo con id '" + torneoId + "'");
        }

        rankings.forEach(r -> r.setEstado(Ranking.Estado.CERRADO));
        List<Ranking> cerrados = this.rankingRepository.saveAll(rankings);
        log.info("Ranking del torneo {} cerrado exitosamente", torneoId);
        return cerrados;
    }


    private void recalcularPosiciones(Long torneoId) {
        List<Ranking> rankings = this.rankingRepository.findByTorneoIdOrderByPuntosDescDiferenciaDesc(torneoId);
        AtomicInteger posicion = new AtomicInteger(1);
        rankings.forEach(r -> {
            r.setPosicion(posicion.getAndIncrement());
            this.rankingRepository.save(r);
        });
        log.info("Posiciones recalculadas para torneo {}", torneoId);
    }
}
