package com.aibert.dosw.domain.model.subject;

import com.aibert.dosw.domain.model.user.Level;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class SubjectProgressSnapshot {
  private UUID id;
  private UUID userId;
  private String subjectId;
  private String subjectName;
  private float subjectProgressPercentage;
  private Level subjectLevel;
  private int xpEarned;
  private float academicPerformance;
  private ProgressVisualization progressVisualization;
  private boolean partialData;
}
