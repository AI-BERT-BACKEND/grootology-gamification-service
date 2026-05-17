package com.aibert.dosw.application.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProgressVisualizationDTO {
  private float progressBarPercent;
  private int xpDisplay;
  private String academicStatus;
  private String statusColor;
  private String tasksCompletedLabel;
}
