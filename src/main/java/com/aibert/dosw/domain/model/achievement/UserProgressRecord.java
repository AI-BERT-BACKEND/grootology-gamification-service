package com.aibert.dosw.domain.model.achievement;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserProgressRecord {
  private String activityType;
  private LocalDateTime completionDate;
  private Integer score;
  private Integer progressPercent;
  private Integer streakDays;
  private String subjectId;
}
