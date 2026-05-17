package com.aibert.dosw.entrypoints.rest.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.aibert.dosw.application.dto.response.ProgressVisualizationDTO;
import com.aibert.dosw.application.dto.response.SubjectProgressItemDTO;
import com.aibert.dosw.application.dto.response.SubjectProgressOverviewDTO;
import com.aibert.dosw.domain.model.user.Level;
import com.aibert.dosw.domain.ports.in.SubjectProgressUseCase;
import com.aibert.dosw.entrypoints.advice.GlobalExceptionHandler;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SubjectProgressController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class SubjectProgressControllerTest {

  @Autowired private MockMvc mockMvc;
  @MockitoBean private SubjectProgressUseCase subjectProgressUseCase;

  @Test
  void getProgressOverview_returns200() throws Exception {
    UUID userId = UUID.randomUUID();
    when(subjectProgressUseCase.getProgressOverview(userId))
        .thenReturn(
            SubjectProgressOverviewDTO.builder()
                .userName("student.controller")
                .userGlobalLevel(Level.NOVATO)
                .totalGlobalXp(0)
                .subjects(List.of())
                .build());

    mockMvc
        .perform(get("/api/v1/gamification/{userId}/subjects/progress", userId))
        .andExpect(status().isOk());
  }

  @Test
  void updateProgress_returns200() throws Exception {
    UUID userId = UUID.randomUUID();
    when(subjectProgressUseCase.updateProgress(org.mockito.ArgumentMatchers.eq(userId), org.mockito.ArgumentMatchers.any()))
        .thenReturn(
            SubjectProgressOverviewDTO.builder()
                .userName("student.controller")
                .userGlobalLevel(Level.NOVATO)
                .totalGlobalXp(0)
                .subjects(List.of())
                .build());

    mockMvc
        .perform(
            post("/api/v1/gamification/{userId}/subjects/progress", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "subjects": [
                        {
                          "subjectId": "math-101",
                          "academicPerformance": 90.0,
                          "completedTasks": [{ "taskId": "t1", "xpValue": 10 }]
                        }
                      ]
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userName").value("student.controller"));
  }

  @Test
  void getSubjectProgress_returns200() throws Exception {
    UUID userId = UUID.randomUUID();
    when(subjectProgressUseCase.getSubjectProgress(userId, "math-101"))
        .thenReturn(
            SubjectProgressItemDTO.builder()
                .subjectId("math-101")
                .subjectName("Mathematics")
                .subjectProgressPercentage(65f)
                .subjectLevel(Level.CONSTANTE)
                .xpEarned(40)
                .academicPerformance(80f)
                .progressVisualization(
                    ProgressVisualizationDTO.builder()
                        .progressBarPercent(65f)
                        .xpDisplay(40)
                        .academicStatus("IN_PROGRESS")
                        .statusColor("orange")
                        .tasksCompletedLabel("2/4 tasks")
                        .build())
                .partialData(false)
                .message(null)
                .build());

    mockMvc
        .perform(get("/api/v1/gamification/{userId}/subjects/{subjectId}/progress", userId, "math-101"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.subjectId").value("math-101"));
  }
}
