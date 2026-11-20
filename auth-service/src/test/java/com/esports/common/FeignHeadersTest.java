package com.esports.common;

import feign.RequestTemplate;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class FeignHeadersTest {
    @Test
    void reconoceElPrefijoInternoAntesDeQueFeignApliqueElDestino() {
        var request = new RequestTemplate();
        request.method("POST");
        request.uri("");
        request.feignTarget(new feign.Target.HardCodedTarget<>(Runnable.class,
                "user-service", "http://user-service/internal/usuarios"));
        new ArenaConfiguration().arenaHeaders("clave-solo-de-prueba").apply(request);
        assertThat(request.headers().get("X-Arena-Internal")).containsExactly("clave-solo-de-prueba");
    }

    @Test
    void agregaLaClaveTambienCuandoFeignYaResolvióLaDireccionCompleta() {
        var request = new RequestTemplate();
        request.method("POST");
        request.target("http://user-service");
        request.uri("/internal/usuarios");
        new ArenaConfiguration().arenaHeaders("clave-solo-de-prueba").apply(request);
        assertThat(request.headers().get("X-Arena-Internal")).containsExactly("clave-solo-de-prueba");
    }

    @Test
    void noEntregaLaClaveInternaALasRutasPublicas() {
        var request = new RequestTemplate();
        request.method("GET");
        request.target("http://user-service");
        request.uri("/api/v1/usuarios/me");
        new ArenaConfiguration().arenaHeaders("clave-solo-de-prueba").apply(request);
        assertThat(request.headers()).doesNotContainKey("X-Arena-Internal");
    }
}
