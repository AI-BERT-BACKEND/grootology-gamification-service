package com.aibert.dosw.infrastructure.adapters.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.aibert.dosw.infrastructure.feign.AcademicServiceClient;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AcademicSummaryFeignAdapterTest {

  @Mock private AcademicServiceClient academicServiceClient;
  @InjectMocks private AcademicSummaryFeignAdapter adapter;

  @Test
  void fetchAcademicSummary_mapsValidResponse() {
    when(academicServiceClient.getAcademicSummary("student-1"))
        .thenReturn(
            new AcademicServiceClient.AcademicApiResponse<>(
                true,
                new AcademicServiceClient.AcademicSummaryResponse(
                    "student-1",
                    4.3,
                    List.of(
                        new AcademicServiceClient.AcademicSubjectResponse(
                            10L,
                            "Math",
                            "2026-1",
                            4.5,
                            List.of(
                                new AcademicServiceClient.EvaluationCutResponse(
                                    1L, "Corte 1", 50.0, 4.2))))),
                "ok",
                null,
                null));

    var result = adapter.fetchAcademicSummary("student-1");

    assertEquals("student-1", result.studentId());
    assertEquals(1, result.subjects().size());
    assertEquals(10L, result.subjects().getFirst().subjectId());
    assertEquals(1, result.subjects().getFirst().cuts().size());
  }

  @Test
  void fetchAcademicSummary_responseNull_throws() {
    when(academicServiceClient.getAcademicSummary("student-2")).thenReturn(null);

    assertThrows(IllegalStateException.class, () -> adapter.fetchAcademicSummary("student-2"));
  }

  @Test
  void fetchAcademicSummary_successFalse_throws() {
    when(academicServiceClient.getAcademicSummary("student-3"))
        .thenReturn(
            new AcademicServiceClient.AcademicApiResponse<>(
                false, null, null, "failure", 500));

    assertThrows(IllegalStateException.class, () -> adapter.fetchAcademicSummary("student-3"));
  }

  @Test
  void fetchAcademicSummary_successWithoutData_throws() {
    when(academicServiceClient.getAcademicSummary("student-4"))
        .thenReturn(
            new AcademicServiceClient.AcademicApiResponse<>(
                true, null, "ok", null, null));

    assertThrows(IllegalStateException.class, () -> adapter.fetchAcademicSummary("student-4"));
  }
}
