package com.aibert.dosw.domain.model.user;

import com.aibert.dosw.domain.model.achievement.UnlockedAchievement;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class GamificationProfile {
  private UUID id;
  private UUID userId;
  private int totalPoints;
  private int currentStreak;
  private LocalDate lastActivityDate;
  private Level globalLevel;
  private List<UnlockedAchievement> achievements;
}
