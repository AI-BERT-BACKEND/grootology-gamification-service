package com.aibert.dosw.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.aibert.dosw.domain.ports.out.AcademicSummaryProviderPort;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AcademicIntegrationServiceTest {

  @Mock private AcademicSummaryProviderPort academicSummaryProvider;

  @InjectMocks private AcademicIntegrationService service;

  @Test
  void checkAcademicFeignConnection_returnsMappedData() {
    UUID userId = UUID.randomUUID();
    when(academicSummaryProvider.fetchAcademicSummary("student-123"))
        .thenReturn(
            new AcademicSummaryProviderPort.AcademicSummary(
                "student-123",
                4.5,
                List.of(
                    new AcademicSummaryProviderPort.AcademicSubject(
                        10L,
                        "Mathematics",
                        "2026-1",
                        4.8,
                        List.of(new AcademicSummaryProviderPort.EvaluationCut(1L, "Cut 1", 30.0, 4.7))))));

    var response = service.checkAcademicFeignConnection(userId, "student-123");

    assertEquals(userId.toString(), response.getUserId());
    assertEquals("student-123", response.getStudentId());
    assertEquals(4.5, response.getAcademicGpa());
    assertEquals(1, response.getTotalSubjects());
    assertEquals("Mathematics", response.getSubjects().getFirst().getSubjectName());
  }
}
