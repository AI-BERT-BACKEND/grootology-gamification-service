package com.aibert.dosw.domain.model.achievement;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class UserProgressRecord {
  private String activityType;
  private LocalDateTime completionDate;
  private Integer score;
  private Integer progressPercent;
  private Integer streakDays;
  private UUID subjectId;
}
