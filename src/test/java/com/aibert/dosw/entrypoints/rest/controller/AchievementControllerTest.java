package com.aibert.dosw.entrypoints.rest.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.aibert.dosw.application.dto.response.AchievementResponseDTO;
import com.aibert.dosw.domain.ports.in.AchievementUseCase;
import com.aibert.dosw.entrypoints.advice.GlobalExceptionHandler;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AchievementController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class AchievementControllerTest {

  @Autowired private MockMvc mockMvc;
  @MockitoBean private AchievementUseCase achievementUseCase;

  @Test
  void getGallery_returns200() throws Exception {
    UUID userId = UUID.randomUUID();
    when(achievementUseCase.getGallery(userId))
        .thenReturn(
            AchievementResponseDTO.builder()
                .username("student.controller")
                .achievementUnlocked(false)
                .achievementGallery(List.of())
                .recentAchievements(List.of())
                .build());

    mockMvc
        .perform(get("/api/v1/gamification/{userId}/achievements", userId))
        .andExpect(status().isOk());
  }
}
