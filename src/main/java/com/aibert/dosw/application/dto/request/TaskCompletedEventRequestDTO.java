package com.aibert.dosw.application.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskCompletedEventRequestDTO {

  private String taskId;

  @NotNull private UUID studentId;

  private String subjectId;

  private String taskType;

  private LocalDateTime completionDate;

  private LocalDateTime dueDate;
}
