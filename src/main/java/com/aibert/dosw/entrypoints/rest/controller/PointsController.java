package com.aibert.dosw.entrypoints.rest.controller;

import com.aibert.dosw.application.dto.request.ActionEventRequestDTO;
import com.aibert.dosw.application.dto.response.ApiErrorResponseDTO;
import com.aibert.dosw.application.dto.response.PointsResponseDTO;
import com.aibert.dosw.domain.ports.in.PointsUseCase;
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
@RequestMapping("/api/v1/gamification/{userId}/points")
@RequiredArgsConstructor
@Tag(name = "AIB-36 Points", description = "Points and XP system (AIB-36)")
@SecurityRequirement(name = "Bearer")
public class PointsController {

  private final PointsUseCase pointsUseCase;

  @Operation(
      summary = "Process academic event",
      description =
          """
          Registers a valid academic action and automatically awards XP, updates total points
          and the active productivity streak. Supports alternate flows FA-01 through FA-05.
          Intended to be called by the tasks or academics microservice after a student action.
          """)
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Event processed (check pointsUpdated for outcome)",
        content = @Content(schema = @Schema(implementation = PointsResponseDTO.class))),
    @ApiResponse(
        responseCode = "400",
        description = "Invalid request payload",
        content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Points update failed (FA-05)",
        content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class)))
  })
  @PostMapping(
      value = "/events",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<PointsResponseDTO> processEvent(
      @PathVariable UUID userId, @Valid @RequestBody ActionEventRequestDTO request) {
    return ResponseEntity.ok(pointsUseCase.processAcademicEvent(userId, request));
  }

  @Operation(
      summary = "Get points summary",
      description =
          "Returns the current total points and active streak for the authenticated student.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Points summary retrieved",
        content = @Content(schema = @Schema(implementation = PointsResponseDTO.class))),
    @ApiResponse(
        responseCode = "404",
        description = "Gamification profile not found",
        content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class)))
  })
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<PointsResponseDTO> getPointsSummary(@PathVariable UUID userId) {
    return ResponseEntity.ok(pointsUseCase.getPointsSummary(userId));
  }
}
