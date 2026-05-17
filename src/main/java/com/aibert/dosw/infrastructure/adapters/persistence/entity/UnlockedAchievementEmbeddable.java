package com.aibert.dosw.infrastructure.adapters.persistence.entity;

import com.aibert.dosw.domain.model.achievement.AchievementEvent;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Embeddable
@Getter
@Setter
public class UnlockedAchievementEmbeddable {

  @Enumerated(EnumType.STRING)
  @Column(name = "achievement_event", nullable = false)
  private AchievementEvent event;

  @Column(name = "unlock_date", nullable = false)
  private LocalDateTime unlockDate;
}
