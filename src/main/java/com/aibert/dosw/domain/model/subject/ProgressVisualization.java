package com.aibert.dosw.domain.model.subject;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProgressVisualization {
  private float progressBarPercent;
  private int xpDisplay;
  private String academicStatus;
  private String statusColor;
  private String tasksCompletedLabel;
}
