package com.aibert.dosw.domain.model.subject;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SubjectProgressInput {
  private String subjectId;
  private String subjectName;
  private List<CompletedTask> completedTasks;
  private Integer totalTasks;
  private Integer xpEarned;
  private Float academicPerformance;
}
