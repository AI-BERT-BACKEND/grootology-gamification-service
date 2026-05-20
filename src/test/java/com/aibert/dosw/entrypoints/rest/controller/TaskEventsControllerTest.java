package com.aibert.dosw.entrypoints.rest.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.aibert.dosw.application.dto.response.PointsResponseDTO;
import com.aibert.dosw.domain.ports.in.PointsUseCase;
import com.aibert.dosw.entrypoints.advice.GlobalExceptionHandler;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TaskEventsController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class TaskEventsControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private PointsUseCase pointsUseCase;

  @Test
  void taskCompleted_returns200() throws Exception {
    UUID userId = UUID.randomUUID();
    UUID taskId = UUID.randomUUID();
    when(pointsUseCase.processAcademicEvent(eq(userId), any()))
        .thenReturn(
            PointsResponseDTO.builder()
                .totalPoints(15)
                .xpEarned(15)
                .currentStreak(1)
                .pointsUpdated(true)
                .build());

    mockMvc
        .perform(
            post("/api/v1/events/task-completed")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "taskId": "%s",
                      "studentId": "%s",
                      "subjectId": "subject-1",
                      "taskType": "TAREA"
                    }
                    """
                        .formatted(taskId, userId)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.pointsUpdated").value(true));
  }

  @Test
  void taskCompleted_missingStudentId_returns400() throws Exception {
    mockMvc
        .perform(
            post("/api/v1/events/task-completed")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"taskId\":\"" + UUID.randomUUID() + "\"}"))
        .andExpect(status().isBadRequest());
  }
}
