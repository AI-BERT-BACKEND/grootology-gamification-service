package com.aibert.dosw.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

@SpringBootTest(
    classes = SecurityConfigContextTest.TestApp.class,
    properties = {
      "jwt.secret=01234567890123456789012345678901",
      "app.cors.allowed-origin-patterns=https://aibert.edu,http://localhost:3000",
      "app.cors.allowed-methods=GET,POST",
      "app.cors.allowed-headers=Authorization,Content-Type",
      "app.cors.allow-credentials=false"
    })
@AutoConfigureMockMvc
@TestPropertySource(properties = {"spring.main.allow-bean-definition-overriding=true"})
class SecurityConfigContextTest {

  @Autowired private SecurityFilterChain securityFilterChain;
  @Autowired private CorsConfigurationSource corsConfigurationSource;
  @Autowired private MockMvc mockMvc;

  @Test
  void securityBeans_areCreated() throws Exception {
    assertNotNull(securityFilterChain);
    assertNotNull(corsConfigurationSource);

    CorsConfiguration cors =
        corsConfigurationSource.getCorsConfiguration(new MockHttpServletRequest("GET", "/ping"));
    assertNotNull(cors);
    assertEquals(List.of("https://aibert.edu", "http://localhost:3000"), cors.getAllowedOriginPatterns());
    assertEquals(List.of("GET", "POST"), cors.getAllowedMethods());
    assertEquals(List.of("Authorization", "Content-Type"), cors.getAllowedHeaders());
    assertEquals(Boolean.FALSE, cors.getAllowCredentials());

    mockMvc.perform(get("/ping")).andExpect(status().isOk());
  }

  @SpringBootApplication(
      scanBasePackageClasses = {SecurityConfig.class, JwtAuthFilter.class, SecurityConfigContextTest.class})
  static class TestApp {
    @RestController
    static class PingController {
      @GetMapping("/ping")
      public String ping() {
        return "ok";
      }
    }
  }
}
