package com.aibert.dosw.entrypoints.rest.controller;

import com.aibert.dosw.application.dto.request.SubjectProgressBatchRequestDTO;
import com.aibert.dosw.application.dto.response.ApiErrorResponseDTO;
import com.aibert.dosw.application.dto.response.SubjectProgressItemDTO;
import com.aibert.dosw.application.dto.response.SubjectProgressOverviewDTO;
import com.aibert.dosw.domain.ports.in.SubjectProgressUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/gamification/{userId}/subjects")
@RequiredArgsConstructor
@Tag(name = "AIB-38 Subject Progress", description = "Subject-level progress visualization (AIB-38)")
@SecurityRequirement(name = "Bearer")
public class SubjectProgressController {

  private final SubjectProgressUseCase subjectProgressUseCase;

  @Operation(
      summary = "Update subject progress",
      description =
          """
          Receives academic data per subject, calculates progress percentage, subject level,
          XP and visual indicators. Persists snapshots for real-time visualization (RN-07).
          """)
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Progress calculated and stored",
        content = @Content(schema = @Schema(implementation = SubjectProgressOverviewDTO.class))),
    @ApiResponse(
        responseCode = "404",
        description = "No subjects provided (FA-01)",
        content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Progress load failed (FA-03)",
        content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class)))
  })
  @PostMapping(
      value = "/progress",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<SubjectProgressOverviewDTO> updateProgress(
      @PathVariable UUID userId, @Valid @RequestBody SubjectProgressBatchRequestDTO request) {
    return ResponseEntity.ok(subjectProgressUseCase.updateProgress(userId, request));
  }

  @Operation(
      summary = "Sync progress from academic service",
      description =
          "Fetches the student academic summary from academic-service and recalculates subject progress snapshots.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Progress synchronized from academic service",
        content = @Content(schema = @Schema(implementation = SubjectProgressOverviewDTO.class))),
    @ApiResponse(
        responseCode = "404",
        description = "No subjects provided by academic service (FA-01)",
        content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Progress load failed (FA-03)",
        content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class)))
  })
  @PostMapping(value = "/progress/sync", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<SubjectProgressOverviewDTO> syncProgressFromAcademic(
      @PathVariable UUID userId,
      @RequestHeader(value = "X-Student-Id", required = false) String studentId) {
    return ResponseEntity.ok(subjectProgressUseCase.syncProgressFromAcademic(userId, studentId));
  }

  @Operation(
      summary = "Get all subjects progress",
      description =
          "Returns progress overview for every registered subject plus the student global level.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Overview retrieved",
        content = @Content(schema = @Schema(implementation = SubjectProgressOverviewDTO.class))),
    @ApiResponse(
        responseCode = "404",
        description = "No subjects registered (FA-01)",
        content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class)))
  })
  @GetMapping(value = "/progress", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<SubjectProgressOverviewDTO> getProgressOverview(@PathVariable UUID userId) {
    return ResponseEntity.ok(subjectProgressUseCase.getProgressOverview(userId));
  }

  @Operation(
      summary = "Get single subject progress",
      description = "Returns progress indicators for one subject by its identifier.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Subject progress retrieved",
        content = @Content(schema = @Schema(implementation = SubjectProgressItemDTO.class))),
    @ApiResponse(
        responseCode = "404",
        description = "Subject not found or invalid data (FA-04)",
        content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class)))
  })
  @GetMapping(value = "/{subjectId}/progress", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<SubjectProgressItemDTO> getSubjectProgress(
      @PathVariable UUID userId, @PathVariable String subjectId) {
    return ResponseEntity.ok(subjectProgressUseCase.getSubjectProgress(userId, subjectId));
  }
}
