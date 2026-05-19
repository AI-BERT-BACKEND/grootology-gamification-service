package com.aibert.dosw.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;

class OpenApiConfigTest {

  @Test
  void gamificationOpenApi_containsExpectedMetadataAndSecurity() {
    OpenApiConfig config = new OpenApiConfig();

    OpenAPI openAPI = config.gamificationOpenApi();

    assertNotNull(openAPI.getInfo());
    assertEquals("AIBERT Gamification Service API", openAPI.getInfo().getTitle());
    assertEquals("1.0.0", openAPI.getInfo().getVersion());
    assertNotNull(openAPI.getComponents());
    assertNotNull(openAPI.getComponents().getSecuritySchemes().get("Bearer"));
    assertTrue(openAPI.getTags().stream().anyMatch(tag -> "AIB-35 Progress".equals(tag.getName())));
  }
}
