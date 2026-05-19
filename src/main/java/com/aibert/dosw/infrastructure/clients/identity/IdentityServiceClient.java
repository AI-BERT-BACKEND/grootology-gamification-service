package com.aibert.dosw.infrastructure.clients.identity;

import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "identity-service-client",
    url = "${clients.identity.base-url:http://localhost:8081}",
    path = "/api/v1/users")
public interface IdentityServiceClient {

  @GetMapping("/{userId}")
  IdentityUserResponse getById(@PathVariable UUID userId);

  record IdentityUserResponse(UUID id, String email, String username) {}
}
