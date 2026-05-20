package com.aibert.dosw.infrastructure.adapters.adapter;

import com.aibert.dosw.domain.ports.out.TaskSummaryProviderPort;
import com.aibert.dosw.infrastructure.feign.TaskServiceClient;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TaskSummaryFeignAdapter implements TaskSummaryProviderPort {

  private final TaskServiceClient taskServiceClient;

  @Override
  public List<TaskSummary> fetchTasksByStudent(String studentId) {
    List<TaskServiceClient.TaskSummaryResponse> response =
        taskServiceClient.getTasksByStudent(studentId, studentId);

    if (response == null || response.isEmpty()) {
      return List.of();
    }

    return response.stream()
        .map(
            task ->
                new TaskSummary(
                    task.id(),
                    task.studentId(),
                    task.subjectId(),
                    task.title(),
                    task.description(),
                    task.taskType(),
                    task.estimatedDurationMinutes(),
                    task.deadline(),
                    task.priority(),
                    task.status(),
                    task.scheduledDate(),
                    task.completedAt(),
                    task.changedAt()))
        .toList();
  }
}
