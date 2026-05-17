package com.aibert.dosw.application.dto.response;

import com.aibert.dosw.domain.model.achievement.AchievementEvent;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AchievementBadgeDTO {
  private AchievementEvent event;
  private String name;
  private String icon;
  private String description;
}
