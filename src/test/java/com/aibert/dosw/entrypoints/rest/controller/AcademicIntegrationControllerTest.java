package com.aibert.dosw.entrypoints.rest.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.aibert.dosw.application.dto.response.AcademicSyncCheckResponseDTO;
import com.aibert.dosw.application.dto.response.AcademicSyncSubjectDTO;
import com.aibert.dosw.domain.ports.in.AcademicIntegrationUseCase;
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

@WebMvcTest(AcademicIntegrationController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class AcademicIntegrationControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private AcademicIntegrationUseCase academicIntegrationUseCase;

  @Test
  void checkFeignConnection_returns200() throws Exception {
    UUID userId = UUID.randomUUID();
    when(academicIntegrationUseCase.checkAcademicFeignConnection(userId, "student-123"))
        .thenReturn(
            AcademicSyncCheckResponseDTO.builder()
                .userId(userId.toString())
                .studentId("student-123")
                .academicGpa(4.2)
                .totalSubjects(1)
                .subjects(
                    List.of(
                        AcademicSyncSubjectDTO.builder()
                            .subjectId(1L)
                            .subjectName("Mathematics")
                            .semester("2026-1")
                            .overallAverage(4.6)
                            .cuts(List.of())
                            .build()))
                .build());

    mockMvc
        .perform(
            get("/api/v1/gamification/{userId}/academic/sync-test", userId)
                .header("X-Student-Id", "student-123"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userId").value(userId.toString()))
        .andExpect(jsonPath("$.studentId").value("student-123"))
        .andExpect(jsonPath("$.totalSubjects").value(1));
  }
}
