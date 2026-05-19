package com.aibert.dosw.infrastructure.clients.identity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.cloud.openfeign.FeignClient;

class IdentityServiceClientTest {

  @Test
  void feignClientAnnotation_isConfigured() {
    FeignClient annotation = IdentityServiceClient.class.getAnnotation(FeignClient.class);

    assertNotNull(annotation);
    assertEquals("identity-service-client", annotation.name());
    assertEquals("${clients.identity.base-url:http://localhost:8081}", annotation.url());
    assertEquals("/api/v1/users", annotation.path());
  }
}
