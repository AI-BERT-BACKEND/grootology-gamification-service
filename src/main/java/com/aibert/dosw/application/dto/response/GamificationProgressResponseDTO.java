package com.aibert.dosw.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "AIB-35 Academic gamification progress response")
public class GamificationProgressResponseDTO {
  @Schema(description = "Accumulated total points", example = "250")
  private final int totalPoints;

  @Schema(description = "Current level according to AIB-35 rules", example = "2")
  private final int currentLevel;

  @Schema(description = "Unlocked and locked badges in the student's gallery")
  private final List<GamificationProgressBadgeDTO> badges;

  @Schema(description = "Progress percentage toward next level", example = "75.0")
  private final float progressToNext;
}
