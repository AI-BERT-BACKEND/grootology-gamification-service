package com.aibert.dosw.entrypoints.rest.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.aibert.dosw.application.dto.response.GamificationProgressBadgeDTO;
import com.aibert.dosw.application.dto.response.GamificationProgressResponseDTO;
import com.aibert.dosw.domain.ports.in.GamificationProgressUseCase;
import com.aibert.dosw.entrypoints.advice.GlobalExceptionHandler;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(GamificationProgressController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class GamificationProgressControllerTest {

  @Autowired private MockMvc mockMvc;
  @MockitoBean private GamificationProgressUseCase gamificationProgressUseCase;

  @Test
  void getProgress_returns200() throws Exception {
    UUID userId = UUID.randomUUID();

    when(gamificationProgressUseCase.getGamificationProgress(userId))
        .thenReturn(
            GamificationProgressResponseDTO.builder()
                .totalPoints(250)
                .currentLevel(2)
                .progressToNext(75f)
                .badges(
                    List.of(
                        GamificationProgressBadgeDTO.builder()
                            .badgeId(UUID.randomUUID())
                            .badgeName("Constante")
                            .icon("badge-task-streak")
                            .description("Completa actividades academicas durante 7 dias consecutivos.")
                            .unlocked(true)
                            .unlockedDate(LocalDate.of(2026, 5, 17))
                            .build()))
                .build());

    mockMvc
        .perform(get("/api/v1/gamification/{userId}/progress", userId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalPoints").value(250))
        .andExpect(jsonPath("$.currentLevel").value(2))
        .andExpect(jsonPath("$.progressToNext").value(75.0))
        .andExpect(jsonPath("$.badges[0].unlocked").value(true));
  }
}
