package com.esports.common;

import feign.Request;
import feign.RequestTemplate;
import feign.Response;
import org.junit.jupiter.api.Test;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class FeignErrorsTest {
    @Test
    void conservaElErrorDelCorreoEnLaRespuestaDeRegistro() {
        var response = respuestaRemota(409, """
                {"mensaje":"Revisa los datos del formulario.","errores":{"email":"Este correo ya está registrado. Inicia sesión o usa otro correo."}}
                """);
        var error = new ArenaConfiguration().remoteErrors().decode("PerfilClient#crear", response);
        var salida = new ArenaErrors().failure(error);

        assertThat(salida.getStatusCode().value()).isEqualTo(409);
        assertThat(salida.getBody().errores()).containsExactlyEntriesOf(
                Map.of("email", "Este correo ya está registrado. Inicia sesión o usa otro correo."));
    }

    @Test
    void conservaElEstadoCuandoElServicioRemotoNoEnviaJson() {
        var response = respuestaRemota(503, "Servicio no disponible");
        var error = new ArenaConfiguration().remoteErrors().decode("PerfilClient#crear", response);
        var salida = new ArenaErrors().failure(error);

        assertThat(salida.getStatusCode().value()).isEqualTo(503);
        assertThat(salida.getBody().errores()).isEmpty();
    }

    private Response respuestaRemota(int status, String body) {
        var request = Request.create(Request.HttpMethod.POST, "http://user-service/internal/usuarios",
                Map.of(), new byte[0], StandardCharsets.UTF_8, new RequestTemplate());
        return Response.builder().status(status).request(request).body(body, StandardCharsets.UTF_8).build();
    }
}
