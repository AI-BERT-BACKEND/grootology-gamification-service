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
import com.aibert.dosw.domain.exceptions.SubjectProgressNotFoundException;
import com.aibert.dosw.domain.model.subject.ProgressVisualization;
import com.aibert.dosw.domain.model.subject.SubjectProgressSnapshot;
import com.aibert.dosw.domain.model.user.GamificationProfile;
import com.aibert.dosw.domain.model.user.Level;
import com.aibert.dosw.domain.ports.out.GamificationRepositoryPort;
import com.aibert.dosw.domain.ports.out.SubjectProgressRepositoryPort;
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

@ExtendWith(MockitoExtension.class)
class SubjectProgressServiceTest {

  @Mock private GamificationRepositoryPort gamificationRepository;
  @Mock private SubjectProgressRepositoryPort subjectProgressRepository;
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
                    .totalPoints(200)
                    .globalLevel(Level.COMPROMETIDO)
                    .achievements(new ArrayList<>())
                    .build()));

    SubjectProgressOverviewDTO response = subjectProgressService.getProgressOverview(userId);

    assertEquals(Level.COMPROMETIDO, response.getUserGlobalLevel());
    assertEquals("math-101", response.getSubjects().getFirst().getSubjectId());
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
