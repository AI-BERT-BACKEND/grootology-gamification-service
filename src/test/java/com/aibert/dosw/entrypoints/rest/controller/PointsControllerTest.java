package com.aibert.dosw.entrypoints.rest.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

@WebMvcTest(PointsController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class PointsControllerTest {

  @Autowired private MockMvc mockMvc;
  @MockitoBean private PointsUseCase pointsUseCase;

  private final UUID userId = UUID.randomUUID();

  @Test
  void processEvent_returns200() throws Exception {
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
            post("/api/v1/gamification/{userId}/points/events", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "actionEvent": "TASK_COMPLETED",
                      "completionDate": "2026-05-17T10:00:00",
                      "userActivityHistory": [
                        {
                          "actionEvent": "SUBJECT_PROGRESS",
                          "completionDate": "2026-05-16T10:00:00"
                        }
                      ]
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.pointsUpdated").value(true));
  }

  @Test
  void processEvent_invalidPayload_returns400() throws Exception {
    mockMvc
        .perform(
            post("/api/v1/gamification/{userId}/points/events", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void getPointsSummary_returns200() throws Exception {
    when(pointsUseCase.getPointsSummary(userId))
        .thenReturn(
            PointsResponseDTO.builder()
                .totalPoints(100)
                .xpEarned(0)
                .currentStreak(3)
                .pointsUpdated(false)
                .build());

    mockMvc
        .perform(get("/api/v1/gamification/{userId}/points", userId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalPoints").value(100));
  }
}
