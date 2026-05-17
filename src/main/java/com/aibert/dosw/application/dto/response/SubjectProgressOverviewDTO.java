package com.aibert.dosw.application.dto.response;

import com.aibert.dosw.domain.model.user.Level;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "AIB-38 Subject progress overview response")
public class SubjectProgressOverviewDTO {

  @Schema(description = "Visible student username for the academic progress profile")
  private final String username;

  @Schema(description = "Student global gamification level across AIBERT")
  private final Level userGlobalLevel;

  @Schema(description = "Total XP accumulated in the gamification profile")
  private final int totalGlobalXp;

  @Schema(description = "Per-subject progress indicators")
  private final List<SubjectProgressItemDTO> subjects;

  @Schema(description = "Optional informational message", nullable = true)
  private final String message;
}
