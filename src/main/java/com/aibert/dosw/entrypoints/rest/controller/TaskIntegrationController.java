package com.aibert.dosw.entrypoints.rest.controller;

import com.aibert.dosw.application.dto.response.ApiErrorResponseDTO;
import com.aibert.dosw.application.dto.response.TaskSyncCheckResponseDTO;
import com.aibert.dosw.domain.ports.in.TaskIntegrationUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/gamification/{userId}/tasks")
@RequiredArgsConstructor
@Tag(
    name = "Task Integration",
    description = "Diagnostic endpoint to validate Feign connectivity with task-service")
@SecurityRequirement(name = "Bearer")
public class TaskIntegrationController {

  private final TaskIntegrationUseCase taskIntegrationUseCase;

  @Operation(
      summary = "Check task-service Feign connection",
      description =
          "Calls task-service via Feign using userId as both path and X-User-Id header value.")
  @ApiResponse(
      responseCode = "200",
      description = "Feign call executed successfully",
      content = @Content(schema = @Schema(implementation = TaskSyncCheckResponseDTO.class)))
  @ApiResponse(
      responseCode = "500",
      description = "Task-service is unavailable or response cannot be parsed",
      content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class)))
  @GetMapping(value = "/sync-test", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<TaskSyncCheckResponseDTO> checkFeignConnection(@PathVariable UUID userId) {
    return ResponseEntity.ok(taskIntegrationUseCase.checkTaskFeignConnection(userId));
  }
}
