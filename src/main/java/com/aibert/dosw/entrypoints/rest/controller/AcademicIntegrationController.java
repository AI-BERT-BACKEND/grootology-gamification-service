package com.aibert.dosw.entrypoints.rest.controller;

import com.aibert.dosw.application.dto.response.AcademicSyncCheckResponseDTO;
import com.aibert.dosw.application.dto.response.ApiErrorResponseDTO;
import com.aibert.dosw.domain.ports.in.AcademicIntegrationUseCase;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/gamification/{userId}/academic")
@RequiredArgsConstructor
@Tag(
    name = "Academic Integration",
    description = "Diagnostic endpoint to validate Feign connectivity with academic-service")
@SecurityRequirement(name = "Bearer")
public class AcademicIntegrationController {

  private final AcademicIntegrationUseCase academicIntegrationUseCase;

  @Operation(
      summary = "Check academic-service Feign connection",
      description =
          "Calls academic-service via Feign and returns raw academic summary mapped to a diagnostic response.")
  @ApiResponse(
      responseCode = "200",
      description = "Feign call executed successfully",
      content = @Content(schema = @Schema(implementation = AcademicSyncCheckResponseDTO.class)))
  @ApiResponse(
      responseCode = "500",
      description = "Academic-service is unavailable or response cannot be parsed",
      content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class)))
  @GetMapping(value = "/sync-test", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AcademicSyncCheckResponseDTO> checkFeignConnection(
      @PathVariable UUID userId,
      @RequestHeader(value = "X-Student-Id", required = false) String studentId) {
    return ResponseEntity.ok(academicIntegrationUseCase.checkAcademicFeignConnection(userId, studentId));
  }
}
