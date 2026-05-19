package com.aibert.dosw.config;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtAuthFilter jwtAuthFilter;

  @Value("#{'${app.cors.allowed-origin-patterns:*}'.split(',')}")
  private List<String> allowedOriginPatterns;

  @Value("#{'${app.cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS}'.split(',')}")
  private List<String> allowedMethods;

  @Value("#{'${app.cors.allowed-headers:*}'.split(',')}")
  private List<String> allowedHeaders;

  @Value("${app.cors.allow-credentials:true}")
  private boolean allowCredentials;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .csrf(AbstractHttpConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)
        .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/actuator/**")
                    .permitAll()
                    .anyRequest()
                    .permitAll()) // TEMPORAL PARA PROBAR ENDPOINTS
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOriginPatterns(trimmed(allowedOriginPatterns));
    config.setAllowedMethods(trimmed(allowedMethods));
    config.setAllowedHeaders(trimmed(allowedHeaders));
    config.setAllowCredentials(allowCredentials);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);

    return source;
  }

  private List<String> trimmed(List<String> values) {
    return values.stream().map(String::trim).filter(v -> !v.isEmpty()).collect(Collectors.toList());
  }
}
