package com.aibert.dosw.entrypoints.rest.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.aibert.dosw.application.dto.response.TaskSyncCheckResponseDTO;
import com.aibert.dosw.application.dto.response.TaskSyncItemDTO;
import com.aibert.dosw.domain.ports.in.TaskIntegrationUseCase;
import com.aibert.dosw.entrypoints.advice.GlobalExceptionHandler;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TaskIntegrationController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class TaskIntegrationControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private TaskIntegrationUseCase taskIntegrationUseCase;

  @Test
  void checkFeignConnection_returns200() throws Exception {
    UUID userId = UUID.randomUUID();
    when(taskIntegrationUseCase.checkTaskFeignConnection(userId))
        .thenReturn(
            TaskSyncCheckResponseDTO.builder()
                .userId(userId.toString())
                .totalTasks(1)
                .tasks(
                    List.of(
                        TaskSyncItemDTO.builder()
                            .id("task-1")
                            .studentId(userId.toString())
                            .subjectId("subject-1")
                            .title("Task 1")
                            .taskType("HOMEWORK")
                            .priority("HIGH")
                            .status("TODO")
                            .deadline(LocalDateTime.of(2026, 5, 20, 10, 0))
                            .build()))
                .build());

    mockMvc
        .perform(get("/api/v1/gamification/{userId}/tasks/sync-test", userId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userId").value(userId.toString()))
        .andExpect(jsonPath("$.totalTasks").value(1))
        .andExpect(jsonPath("$.tasks[0].id").value("task-1"));
  }
}
