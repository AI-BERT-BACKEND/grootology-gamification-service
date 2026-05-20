package com.aibert.dosw.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.aibert.dosw.domain.ports.out.TaskSummaryProviderPort;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TaskIntegrationServiceTest {

  @Mock private TaskSummaryProviderPort taskSummaryProvider;

  @InjectMocks private TaskIntegrationService service;

  @Test
  void checkTaskFeignConnection_returnsMappedResponse() {
    UUID userId = UUID.randomUUID();
    String studentId = userId.toString();
    LocalDateTime now = LocalDateTime.of(2026, 5, 19, 18, 45);

    when(taskSummaryProvider.fetchTasksByStudent(studentId))
        .thenReturn(
            List.of(
                new TaskSummaryProviderPort.TaskSummary(
                    "task-1",
                    studentId,
                    "subject-1",
                    "Task 1",
                    "Description",
                    "HOMEWORK",
                    40,
                    now.plusDays(1),
                    "MEDIUM",
                    "IN_PROGRESS",
                    now,
                    null,
                    now)));

    var response = service.checkTaskFeignConnection(userId);

    assertEquals(studentId, response.getUserId());
    assertEquals(1, response.getTotalTasks());
    assertEquals("task-1", response.getTasks().getFirst().getId());
    assertEquals("IN_PROGRESS", response.getTasks().getFirst().getStatus());
  }
}
