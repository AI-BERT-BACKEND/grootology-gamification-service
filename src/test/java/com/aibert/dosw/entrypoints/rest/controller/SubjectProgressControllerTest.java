package com.aibert.dosw.entrypoints.rest.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
                .username("student.controller")
                .userGlobalLevel(Level.NOVATO)
                .totalGlobalXp(0)
                .subjects(List.of())
                .build());

    mockMvc
        .perform(get("/api/v1/gamification/{userId}/subjects/progress", userId))
        .andExpect(status().isOk());
  }
}
