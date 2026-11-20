package com.esports.registrationservice;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import com.esports.registrationservice.clients.*;
import com.esports.registrationservice.models.*;
import com.esports.registrationservice.models.dtos.*;
import com.esports.registrationservice.repositories.*;
import com.esports.registrationservice.services.*;
import java.util.*;
import java.util.concurrent.*;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
@SpringBootTest class CuposConcurrentesTest {
 @Autowired InscripcionService service; @Autowired InscripcionRepository repo;
 @MockitoBean TorneoClient torneos; @MockitoBean UsuarioClient usuarios; @MockitoBean SancionClient sanciones;
 @MockitoBean EquipoClient equipos; @MockitoBean JuegoClient juegos;
 @Test void dosPeticionesNoPuedenOcuparElUltimoCupo() throws Exception {
  repo.deleteAll(); var t=new TorneoDTO(); t.setTorneoId(900L); t.setEstado("ABIERTO"); t.setModalidad("INDIVIDUAL"); t.setCupoMaximo(1); t.setFechaCierreInscripcion(java.time.LocalDateTime.now().plusDays(1));
  when(torneos.getTorneoById(900L)).thenReturn(t); var u=new UsuarioDTO(); u.setEstado("ACTIVO"); when(usuarios.getUsuarioById(anyLong())).thenReturn(u); when(sanciones.verificarSancion(anyLong(),isNull())).thenReturn(Map.of("sancionado",false));
  var latch=new CountDownLatch(1); try(var pool=Executors.newFixedThreadPool(2)) {
   var futures=new ArrayList<Future<Boolean>>();
   for(long id:List.of(1L,2L)) futures.add(pool.submit(() -> { latch.await(); var dto=new InscripcionDTO(); dto.setTorneoId(900L); dto.setJugadorId(id); dto.setTipoParticipante(Inscripcion.TipoParticipante.INDIVIDUAL); try { service.save(dto); return true; } catch(com.esports.registrationservice.exceptions.InscripcionException ex) { return false; } }));
   latch.countDown(); int aceptadas=0; for(var f:futures) if(f.get(20,TimeUnit.SECONDS)) aceptadas++;
   assertThat(aceptadas).isEqualTo(1); assertThat(repo.countByTorneoIdAndEstado(900L,Inscripcion.Estado.PENDIENTE)).isEqualTo(1);
  }
 }
}
