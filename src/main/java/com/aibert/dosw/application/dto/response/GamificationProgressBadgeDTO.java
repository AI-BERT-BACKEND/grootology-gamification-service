package com.aibert.dosw.application.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GamificationProgressBadgeDTO {
  private final UUID badgeId;
  private final String badgeName;
  private final String icon;
  private final String description;
  private final boolean unlocked;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  private final LocalDate unlockedDate;
}
