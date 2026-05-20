package com.aibert.dosw.infrastructure.feign;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.cloud.openfeign.FeignClient;

class TaskServiceClientTest {

  @Test
  void feignClientAnnotation_isConfigured() {
    FeignClient annotation = TaskServiceClient.class.getAnnotation(FeignClient.class);

    assertNotNull(annotation);
    assertEquals("task-service-client", annotation.name());
    assertEquals("${clients.task.base-url:http://localhost:1503}", annotation.url());
    assertEquals("/api/tasks", annotation.path());
  }
}
