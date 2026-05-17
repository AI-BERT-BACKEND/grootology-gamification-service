package com.aibert.dosw.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "AIB-37 Achievement System response")
public class AchievementResponseDTO {
  @Schema(description = "Whether the achievement was unlocked in this request")
  private final boolean achievementUnlocked;

  @Schema(description = "Badge metadata for the processed achievement")
  private final AchievementBadgeDTO achievementBadge;

  @Schema(description = "Full gallery with unlocked and pending achievements")
  private final List<AchievementGalleryItemDTO> achievementGallery;

  @Schema(description = "Recently unlocked achievements")
  private final List<AchievementGalleryItemDTO> recentAchievements;

  @Schema(description = "Informational message for alternate flows", nullable = true)
  private final String message;
}
