package com.aibert.dosw.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "AIB-36 Points System response")
public class PointsResponseDTO {

  @Schema(description = "Accumulated total points", example = "150")
  private final int totalPoints;

  @Schema(description = "XP earned in the current action", example = "15")
  private final int xpEarned;

  @Schema(description = "Active productivity streak in consecutive days", example = "3")
  private final int currentStreak;

  @Schema(
      description = "Whether points were updated successfully",
      example = "true")
  private final boolean pointsUpdated;

  @Schema(
      description = "Informational message for alternate flows (FA-01, FA-04, FA-05)",
      nullable = true)
  private final String message;
}
