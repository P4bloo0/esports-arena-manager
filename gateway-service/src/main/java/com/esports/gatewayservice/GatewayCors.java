package com.esports.gatewayservice;
import org.springframework.context.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.cors.*;
import org.springframework.web.filter.CorsFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import java.util.List;
@Configuration public class GatewayCors {
 @Bean FilterRegistrationBean<CorsFilter> cors(@Value("${arena.cors-origins:http://localhost:5173}") String origins) {
  var config=new CorsConfiguration(); config.setAllowedOrigins(java.util.Arrays.asList(origins.split(","))); config.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS")); config.setAllowedHeaders(List.of("Authorization","Content-Type")); config.setMaxAge(3600L);
  var source=new UrlBasedCorsConfigurationSource(); source.registerCorsConfiguration("/**",config);
  var filter=new FilterRegistrationBean<>(new CorsFilter(source)); filter.setOrder(org.springframework.core.Ordered.HIGHEST_PRECEDENCE); return filter;
 }
}
