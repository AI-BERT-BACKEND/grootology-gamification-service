package com.aibert.dosw.application.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TaskSyncItemDTO {
  private final String id;
  private final String studentId;
  private final String subjectId;
  private final String title;
  private final String taskType;
  private final String priority;
  private final String status;
  private final LocalDateTime deadline;
  private final LocalDateTime scheduledDate;
  private final LocalDateTime completedAt;
  private final LocalDateTime changedAt;
}
