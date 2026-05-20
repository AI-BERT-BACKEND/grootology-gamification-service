package com.aibert.dosw.infrastructure.adapters.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.aibert.dosw.infrastructure.feign.TaskServiceClient;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TaskSummaryFeignAdapterTest {

  @Mock private TaskServiceClient taskServiceClient;

  @InjectMocks private TaskSummaryFeignAdapter adapter;

  @Test
  void fetchTasksByStudent_mapsResponse() {
    String studentId = "student-1";
    LocalDateTime now = LocalDateTime.of(2026, 5, 19, 18, 40);
    when(taskServiceClient.getTasksByStudent(studentId, studentId))
        .thenReturn(
            List.of(
                new TaskServiceClient.TaskSummaryResponse(
                    "task-1",
                    studentId,
                    "subject-1",
                    "Task 1",
                    "Description",
                    "HOMEWORK",
                    45,
                    now.plusDays(1),
                    "HIGH",
                    "TODO",
                    now,
                    null,
                    now)));

    var result = adapter.fetchTasksByStudent(studentId);

    assertEquals(1, result.size());
    assertEquals("task-1", result.getFirst().id());
    assertEquals("TODO", result.getFirst().status());
    verify(taskServiceClient).getTasksByStudent(studentId, studentId);
  }

  @Test
  void fetchTasksByStudent_nullResponse_returnsEmptyList() {
    String studentId = "student-1";
    when(taskServiceClient.getTasksByStudent(studentId, studentId)).thenReturn(null);

    var result = adapter.fetchTasksByStudent(studentId);

    assertTrue(result.isEmpty());
  }
}
