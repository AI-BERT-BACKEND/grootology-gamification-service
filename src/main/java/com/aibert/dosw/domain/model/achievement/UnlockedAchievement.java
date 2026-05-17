package com.aibert.dosw.domain.model.achievement;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UnlockedAchievement {
  private AchievementEvent event;
  private LocalDateTime unlockDate;
}
