package com.aibert.dosw.entrypoints.rest.controller;

import com.aibert.dosw.application.dto.request.AchievementUnlockRequestDTO;
import com.aibert.dosw.application.dto.response.AchievementResponseDTO;
import com.aibert.dosw.application.dto.response.ApiErrorResponseDTO;
import com.aibert.dosw.domain.ports.in.AchievementUseCase;
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
@RequestMapping("/api/v1/gamification/{userId}/achievements")
@RequiredArgsConstructor
@Tag(name = "AIB-37 Achievements", description = "Achievement and badge system (AIB-37)")
@SecurityRequirement(name = "Bearer")
public class AchievementController {

  private final AchievementUseCase achievementUseCase;

  @Operation(
      summary = "Unlock achievement",
      description =
          """
          Evaluates academic progress data and unlocks the corresponding achievement when all
          conditions are met. Prevents duplicate unlocks (FA-02) and ignores invalid events (FA-04).
          """)
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Unlock attempt processed (check achievementUnlocked)",
        content = @Content(schema = @Schema(implementation = AchievementResponseDTO.class))),
    @ApiResponse(
        responseCode = "400",
        description = "Invalid request payload",
        content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Achievement update failed (FA-03)",
        content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class)))
  })
  @PostMapping(
      value = "/unlock",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AchievementResponseDTO> unlockAchievement(
      @PathVariable UUID userId, @Valid @RequestBody AchievementUnlockRequestDTO request) {
    return ResponseEntity.ok(achievementUseCase.unlockAchievement(userId, request));
  }

  @Operation(
      summary = "Get achievement gallery",
      description =
          "Returns the full achievement gallery including unlocked and pending achievements, "
              + "plus recently unlocked items.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Gallery retrieved",
        content = @Content(schema = @Schema(implementation = AchievementResponseDTO.class))),
    @ApiResponse(
        responseCode = "404",
        description = "Gamification profile not found",
        content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class)))
  })
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AchievementResponseDTO> getGallery(@PathVariable UUID userId) {
    return ResponseEntity.ok(achievementUseCase.getGallery(userId));
  }
}
