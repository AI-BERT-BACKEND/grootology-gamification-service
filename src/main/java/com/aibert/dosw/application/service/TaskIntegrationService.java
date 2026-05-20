package com.aibert.dosw.application.service;

import com.aibert.dosw.application.dto.response.TaskSyncCheckResponseDTO;
import com.aibert.dosw.application.dto.response.TaskSyncItemDTO;
import com.aibert.dosw.domain.ports.in.TaskIntegrationUseCase;
import com.aibert.dosw.domain.ports.out.TaskSummaryProviderPort;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskIntegrationService implements TaskIntegrationUseCase {

  private final TaskSummaryProviderPort taskSummaryProvider;

  @Override
  public TaskSyncCheckResponseDTO checkTaskFeignConnection(UUID userId) {
    String studentId = userId.toString();
    List<TaskSyncItemDTO> tasks =
        taskSummaryProvider.fetchTasksByStudent(studentId).stream()
            .map(
                task ->
                    TaskSyncItemDTO.builder()
                        .id(task.id())
                        .studentId(task.studentId())
                        .subjectId(task.subjectId())
                        .title(task.title())
                        .taskType(task.taskType())
                        .priority(task.priority())
                        .status(task.status())
                        .deadline(task.deadline())
                        .scheduledDate(task.scheduledDate())
                        .completedAt(task.completedAt())
                        .changedAt(task.changedAt())
                        .build())
            .toList();

    return TaskSyncCheckResponseDTO.builder()
        .userId(studentId)
        .totalTasks(tasks.size())
        .tasks(tasks)
        .build();
  }
}
