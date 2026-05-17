package com.aibert.dosw.application.dto.response;

import com.aibert.dosw.domain.model.user.Level;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SubjectProgressItemDTO {
  private String subjectId;
  private String subjectName;
  private float subjectProgressPercentage;
  private Level subjectLevel;
  private int xpEarned;
  private float academicPerformance;
  private ProgressVisualizationDTO progressVisualization;
  private boolean partialData;
  private String message;
}
