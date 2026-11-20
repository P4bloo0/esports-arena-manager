package com.esports.registrationservice.config;
import org.springframework.context.annotation.*;
import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.*;
@Configuration public class SwaggerConfig {
 @Bean OpenAPI customOpenApi() { return new OpenAPI().info(new Info().title("eSports: registration-service").version("EP3"))
  .components(new Components().addSecuritySchemes("bearerAuth",new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
  .addSecurityItem(new SecurityRequirement().addList("bearerAuth")); }
}
