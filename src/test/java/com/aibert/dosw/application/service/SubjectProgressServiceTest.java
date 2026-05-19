package com.aibert.dosw.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.aibert.dosw.application.dto.request.CompletedTaskDTO;
import com.aibert.dosw.application.dto.request.SubjectProgressBatchRequestDTO;
import com.aibert.dosw.application.dto.request.SubjectProgressDataDTO;
import com.aibert.dosw.application.dto.response.SubjectProgressOverviewDTO;
import com.aibert.dosw.application.mapper.SubjectProgressApplicationMapper;
import com.aibert.dosw.domain.exceptions.NoSubjectsRegisteredException;
import com.aibert.dosw.domain.exceptions.SubjectProgressLoadException;
import com.aibert.dosw.domain.exceptions.SubjectProgressNotFoundException;
import com.aibert.dosw.domain.model.subject.ProgressVisualization;
import com.aibert.dosw.domain.model.subject.SubjectProgressSnapshot;
import com.aibert.dosw.domain.model.user.GamificationProfile;
import com.aibert.dosw.domain.model.user.Level;
import com.aibert.dosw.domain.ports.out.GamificationRepositoryPort;
import com.aibert.dosw.domain.ports.out.SubjectProgressRepositoryPort;
import com.aibert.dosw.infrastructure.feign.AcademicServiceClient;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class SubjectProgressServiceTest {

  @Mock private GamificationRepositoryPort gamificationRepository;
  @Mock private SubjectProgressRepositoryPort subjectProgressRepository;
  @Mock private AcademicServiceClient academicServiceClient;
  @Spy private SubjectProgressApplicationMapper subjectProgressMapper =
      Mappers.getMapper(SubjectProgressApplicationMapper.class);
  @InjectMocks private SubjectProgressService subjectProgressService;

  private final UUID userId = UUID.randomUUID();

  @Test
  void updateProgress_persistsSnapshot() {
    when(gamificationRepository.findByUserId(userId)).thenReturn(Optional.empty());
    when(subjectProgressRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    SubjectProgressOverviewDTO response =
        subjectProgressService.updateProgress(userId, buildBatch());

    assertEquals(userId.toString(), response.getUserName());
    assertEquals(1, response.getSubjects().size());
    verify(subjectProgressRepository).save(any());
  }

  @Test
  void updateProgress_emptySubjects_fa01() {
    SubjectProgressBatchRequestDTO request = new SubjectProgressBatchRequestDTO();
    request.setSubjects(List.of());
    assertThrows(
        NoSubjectsRegisteredException.class, () -> subjectProgressService.updateProgress(userId, request));
  }

  @Test
  void getSubjectProgress_notFound_fa04() {
    when(subjectProgressRepository.findByUserIdAndSubjectId(userId, "missing"))
        .thenReturn(Optional.empty());
    assertThrows(
        SubjectProgressNotFoundException.class,
        () -> subjectProgressService.getSubjectProgress(userId, "missing"));
  }

  @Test
  void getProgressOverview_returnsSnapshots() {
    when(subjectProgressRepository.findByUserId(userId)).thenReturn(List.of(sampleSnapshot()));
    when(gamificationRepository.findByUserId(userId))
        .thenReturn(
            Optional.of(
                GamificationProfile.builder()
                    .userId(userId)
                    .username("student.progress")
                    .totalPoints(200)
                    .globalLevel(Level.COMPROMETIDO)
                    .achievements(new ArrayList<>())
                    .build()));

    SubjectProgressOverviewDTO response = subjectProgressService.getProgressOverview(userId);

    assertEquals("student.progress", response.getUserName());
    assertEquals(Level.COMPROMETIDO, response.getUserGlobalLevel());
    assertEquals("math-101", response.getSubjects().getFirst().getSubjectId());
  }

  @Test
  void getProgressOverview_withoutSnapshots_throwsNoSubjectsRegistered() {
    when(subjectProgressRepository.findByUserId(userId)).thenReturn(List.of());

    assertThrows(
        NoSubjectsRegisteredException.class, () -> subjectProgressService.getProgressOverview(userId));
  }

  @Test
  void updateProgress_usesAuthenticatedPrincipalWhenProfileUsernameMissing() {
    SecurityContextHolder.getContext()
        .setAuthentication(new UsernamePasswordAuthenticationToken("student.auth", null, List.of()));
    try {
      when(gamificationRepository.findByUserId(userId)).thenReturn(Optional.empty());
      when(subjectProgressRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

      SubjectProgressOverviewDTO response = subjectProgressService.updateProgress(userId, buildBatch());

      assertEquals("student.auth", response.getUserName());
    } finally {
      SecurityContextHolder.clearContext();
    }
  }

  @Test
  void getSubjectProgress_returnsSnapshot() {
    when(subjectProgressRepository.findByUserIdAndSubjectId(userId, "math-101"))
        .thenReturn(Optional.of(sampleSnapshot()));

    var item = subjectProgressService.getSubjectProgress(userId, "math-101");

    assertEquals("math-101", item.getSubjectId());
    assertEquals("Mathematics", item.getSubjectName());
  }

  @Test
  void syncProgressFromAcademic_usesAcademicSummaryData() {
    when(gamificationRepository.findByUserId(userId)).thenReturn(Optional.empty());
    when(subjectProgressRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    when(academicServiceClient.getAcademicSummary("student-1"))
        .thenReturn(
            new AcademicServiceClient.AcademicApiResponse<>(
                true,
                new AcademicServiceClient.AcademicSummaryResponse(
                    "student-1",
                    4.3,
                    List.of(
                        new AcademicServiceClient.AcademicSubjectResponse(
                            101L,
                            "Mathematics",
                            "2026-1",
                            4.5,
                            List.of(
                                new AcademicServiceClient.EvaluationCutResponse(
                                    1L, "Corte 1", 50.0, 4.2),
                                new AcademicServiceClient.EvaluationCutResponse(
                                    2L, "Corte 2", 50.0, null))))),
                "ok",
                null,
                null));

    SubjectProgressOverviewDTO response =
        subjectProgressService.syncProgressFromAcademic(userId, "student-1");

    assertEquals(1, response.getSubjects().size());
    assertEquals("101", response.getSubjects().getFirst().getSubjectId());
    verify(academicServiceClient).getAcademicSummary("student-1");
    verify(subjectProgressRepository).save(any());
  }

  @Test
  void syncProgressFromAcademic_blankStudentId_usesUserIdAndClampsPerformanceTo100() {
    when(gamificationRepository.findByUserId(userId)).thenReturn(Optional.empty());
    when(subjectProgressRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    when(academicServiceClient.getAcademicSummary(userId.toString()))
        .thenReturn(
            new AcademicServiceClient.AcademicApiResponse<>(
                true,
                new AcademicServiceClient.AcademicSummaryResponse(
                    userId.toString(),
                    5.9,
                    List.of(
                        new AcademicServiceClient.AcademicSubjectResponse(
                            101L,
                            "Mathematics",
                            "2026-1",
                            6.0,
                            List.of(
                                new AcademicServiceClient.EvaluationCutResponse(
                                    1L, "Corte 1", 50.0, 4.8))))),
                "ok",
                null,
                null));

    SubjectProgressOverviewDTO response =
        subjectProgressService.syncProgressFromAcademic(userId, "   ");

    verify(academicServiceClient).getAcademicSummary(userId.toString());
    assertEquals(100f, response.getSubjects().getFirst().getAcademicPerformance());
  }

  @Test
  void syncProgressFromAcademic_nullOverallAverage_setsAcademicPerformanceToZero() {
    when(gamificationRepository.findByUserId(userId)).thenReturn(Optional.empty());
    when(subjectProgressRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    when(academicServiceClient.getAcademicSummary("student-2"))
        .thenReturn(
            new AcademicServiceClient.AcademicApiResponse<>(
                true,
                new AcademicServiceClient.AcademicSummaryResponse(
                    "student-2",
                    null,
                    List.of(
                        new AcademicServiceClient.AcademicSubjectResponse(
                            202L,
                            "Physics",
                            "2026-1",
                            null,
                            List.of(
                                new AcademicServiceClient.EvaluationCutResponse(
                                    1L, "Corte 1", 50.0, 4.0))))),
                "ok",
                null,
                null));

    SubjectProgressOverviewDTO response =
        subjectProgressService.syncProgressFromAcademic(userId, "student-2");

    assertEquals(0f, response.getSubjects().getFirst().getAcademicPerformance());
  }

  @Test
  void syncProgressFromAcademic_noSubjects_throwsNoSubjectsRegistered() {
    when(academicServiceClient.getAcademicSummary("student-empty"))
        .thenReturn(
            new AcademicServiceClient.AcademicApiResponse<>(
                true,
                new AcademicServiceClient.AcademicSummaryResponse(
                    "student-empty", 3.5, List.of()),
                "ok",
                null,
                null));

    assertThrows(
        NoSubjectsRegisteredException.class,
        () -> subjectProgressService.syncProgressFromAcademic(userId, "student-empty"));
  }

  @Test
  void syncProgressFromAcademic_onlyNullSubjectIds_throwsNoSubjectsRegistered() {
    when(academicServiceClient.getAcademicSummary("student-null-subject"))
        .thenReturn(
            new AcademicServiceClient.AcademicApiResponse<>(
                true,
                new AcademicServiceClient.AcademicSummaryResponse(
                    "student-null-subject",
                    4.0,
                    List.of(
                        new AcademicServiceClient.AcademicSubjectResponse(
                            null, "Unknown", "2026-1", 4.0, List.of()))),
                "ok",
                null,
                null));

    assertThrows(
        NoSubjectsRegisteredException.class,
        () -> subjectProgressService.syncProgressFromAcademic(userId, "student-null-subject"));
  }

  @Test
  void syncProgressFromAcademic_runtimeFailure_wrapsAsSubjectProgressLoadException() {
    when(academicServiceClient.getAcademicSummary("student-error"))
        .thenThrow(new RuntimeException("feign failure"));

    assertThrows(
        SubjectProgressLoadException.class,
        () -> subjectProgressService.syncProgressFromAcademic(userId, "student-error"));
  }

  @Test
  void syncProgressFromAcademic_withEmptyCuts_keepsZeroTasks() {
    when(gamificationRepository.findByUserId(userId)).thenReturn(Optional.empty());
    when(subjectProgressRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    when(academicServiceClient.getAcademicSummary("student-empty-cuts"))
        .thenReturn(
            new AcademicServiceClient.AcademicApiResponse<>(
                true,
                new AcademicServiceClient.AcademicSummaryResponse(
                    "student-empty-cuts",
                    3.2,
                    List.of(
                        new AcademicServiceClient.AcademicSubjectResponse(
                            303L, "Chemistry", "2026-1", 4.0, List.of()))),
                "ok",
                null,
                null));

    SubjectProgressOverviewDTO response =
        subjectProgressService.syncProgressFromAcademic(userId, "student-empty-cuts");

    assertEquals(1, response.getSubjects().size());
    assertEquals("0/1 tasks", response.getSubjects().getFirst().getProgressVisualization().getTasksCompletedLabel());
  }

  @Test
  void syncProgressFromAcademic_cutWithoutId_usesCutNameFallbackId() {
    when(gamificationRepository.findByUserId(userId)).thenReturn(Optional.empty());
    when(subjectProgressRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    when(academicServiceClient.getAcademicSummary("student-cut-name"))
        .thenReturn(
            new AcademicServiceClient.AcademicApiResponse<>(
                true,
                new AcademicServiceClient.AcademicSummaryResponse(
                    "student-cut-name",
                    4.1,
                    List.of(
                        new AcademicServiceClient.AcademicSubjectResponse(
                            404L,
                            "History",
                            "2026-1",
                            4.2,
                            List.of(
                                new AcademicServiceClient.EvaluationCutResponse(
                                    null, "Corte Especial", 100.0, 4.0))))),
                "ok",
                null,
                null));

    SubjectProgressOverviewDTO response =
        subjectProgressService.syncProgressFromAcademic(userId, "student-cut-name");

    assertEquals(1, response.getSubjects().size());
    verify(subjectProgressRepository).save(any());
  }

  private SubjectProgressSnapshot sampleSnapshot() {
    return SubjectProgressSnapshot.builder()
        .userId(userId)
        .subjectId("math-101")
        .subjectName("Mathematics")
        .subjectProgressPercentage(65f)
        .subjectLevel(Level.CONSTANTE)
        .xpEarned(40)
        .academicPerformance(80f)
        .progressVisualization(
            ProgressVisualization.builder()
                .progressBarPercent(65f)
                .xpDisplay(40)
                .academicStatus("IN_PROGRESS")
                .statusColor("orange")
                .tasksCompletedLabel("2/4 tasks")
                .build())
        .partialData(false)
        .build();
  }

  private SubjectProgressBatchRequestDTO buildBatch() {
    CompletedTaskDTO task = new CompletedTaskDTO();
    task.setTaskId("t1");
    task.setXpValue(10);

    SubjectProgressDataDTO subject = new SubjectProgressDataDTO();
    subject.setSubjectId("math-101");
    subject.setSubjectName("Mathematics");
    subject.setCompletedTasks(List.of(task));
    subject.setTotalTasks(4);
    subject.setAcademicPerformance(90f);

    SubjectProgressBatchRequestDTO batch = new SubjectProgressBatchRequestDTO();
    batch.setSubjects(List.of(subject));
    return batch;
  }
}
