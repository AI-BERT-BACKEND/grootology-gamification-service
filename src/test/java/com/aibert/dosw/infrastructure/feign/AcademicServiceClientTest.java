package com.aibert.dosw.infrastructure.feign;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.cloud.openfeign.FeignClient;

class AcademicServiceClientTest {

  @Test
  void feignClientAnnotation_isConfigured() {
    FeignClient annotation = AcademicServiceClient.class.getAnnotation(FeignClient.class);

    assertNotNull(annotation);
    assertEquals("academic-service-client", annotation.name());
    assertEquals("${clients.academic.base-url:http://localhost:1502}", annotation.url());
    assertEquals("/api/v1/academic", annotation.path());
  }
}
