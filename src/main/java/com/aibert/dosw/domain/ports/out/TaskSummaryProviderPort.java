package com.aibert.dosw.domain.ports.out;

import java.time.LocalDateTime;
import java.util.List;

public interface TaskSummaryProviderPort {

  List<TaskSummary> fetchTasksByStudent(String studentId);

  record TaskSummary(
      String id,
      String studentId,
      String subjectId,
      String title,
      String description,
      String taskType,
      Integer estimatedDurationMinutes,
      LocalDateTime deadline,
      String priority,
      String status,
      LocalDateTime scheduledDate,
      LocalDateTime completedAt,
      LocalDateTime changedAt) {}
}
