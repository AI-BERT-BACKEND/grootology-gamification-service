package com.aibert.dosw.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SubjectProgressDataDTO {
  @NotBlank private String subjectId;
  private String subjectName;

  @NotNull @Valid private List<CompletedTaskDTO> completedTasks;

  private Integer totalTasks;
  private Integer xpEarned;

  @NotNull private Float academicPerformance;
}
