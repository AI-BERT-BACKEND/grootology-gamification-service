package com.aibert.dosw.domain.model.achievement;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AchievementDefinition {
  private AchievementEvent event;
  private String name;
  private String icon;
  private String description;
}
