package com.aibert.dosw.infrastructure.adapters.adapter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.aibert.dosw.domain.model.subject.ProgressVisualization;
import com.aibert.dosw.domain.model.subject.SubjectProgressSnapshot;
import com.aibert.dosw.domain.model.user.Level;
import com.aibert.dosw.infrastructure.adapters.persistence.entity.SubjectProgressEntity;
import com.aibert.dosw.infrastructure.adapters.persistence.mapper.SubjectProgressPersistenceMapper;
import com.aibert.dosw.infrastructure.adapters.persistence.repository.SubjectProgressJpaRepository;
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
class SubjectProgressRepositoryAdapterTest {

  @Mock private SubjectProgressJpaRepository jpaRepository;
  @Spy private SubjectProgressPersistenceMapper mapper =
      Mappers.getMapper(SubjectProgressPersistenceMapper.class);
  @InjectMocks private SubjectProgressRepositoryAdapter adapter;

  @Test
  void save_createsNewEntity() {
    SubjectProgressSnapshot snapshot = sampleSnapshot();
    when(jpaRepository.findByUserIdAndSubjectId(snapshot.getUserId(), snapshot.getSubjectId()))
        .thenReturn(Optional.empty());
    when(jpaRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    SubjectProgressSnapshot saved = adapter.save(snapshot);

    assertEquals("math-101", saved.getSubjectId());
    verify(jpaRepository).save(any(SubjectProgressEntity.class));
  }

  @Test
  void save_updatesExistingEntity() {
    SubjectProgressSnapshot snapshot = sampleSnapshot();
    SubjectProgressEntity existing =
        SubjectProgressEntity.builder()
            .userId(snapshot.getUserId())
            .subjectId(snapshot.getSubjectId())
            .subjectName("Old")
            .subjectProgressPercentage(10f)
            .subjectLevel(Level.NOVATO)
            .xpEarned(5)
            .academicPerformance(50f)
            .progressBarPercent(10f)
            .xpDisplay(5)
            .academicStatus("INITIAL")
            .statusColor("yellow")
            .tasksCompletedLabel("0/1")
            .partialData(true)
            .build();

    when(jpaRepository.findByUserIdAndSubjectId(snapshot.getUserId(), snapshot.getSubjectId()))
        .thenReturn(Optional.of(existing));
    when(jpaRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    SubjectProgressSnapshot saved = adapter.save(snapshot);

    assertEquals("math-101", saved.getSubjectId());
    verify(jpaRepository).save(existing);
  }

  @Test
  void findByUserIdAndSubjectId_returnsMappedSnapshot() {
    SubjectProgressEntity entity =
        SubjectProgressEntity.builder()
            .userId(UUID.randomUUID())
            .subjectId("math-101")
            .subjectName("Mathematics")
            .subjectProgressPercentage(50f)
            .subjectLevel(Level.NOVATO)
            .xpEarned(10)
            .academicPerformance(70f)
            .progressBarPercent(50f)
            .xpDisplay(10)
            .academicStatus("IN_PROGRESS")
            .statusColor("orange")
            .tasksCompletedLabel("1/2 tasks")
            .partialData(false)
            .build();

    when(jpaRepository.findByUserIdAndSubjectId(entity.getUserId(), entity.getSubjectId()))
        .thenReturn(Optional.of(entity));

    Optional<SubjectProgressSnapshot> result =
        adapter.findByUserIdAndSubjectId(entity.getUserId(), entity.getSubjectId());

    assertTrue(result.isPresent());
    assertEquals(50f, result.get().getSubjectProgressPercentage());
  }

  private SubjectProgressSnapshot sampleSnapshot() {
    UUID userId = UUID.randomUUID();
    return SubjectProgressSnapshot.builder()
        .userId(userId)
        .subjectId("math-101")
        .subjectName("Mathematics")
        .subjectProgressPercentage(40f)
        .subjectLevel(Level.NOVATO)
        .xpEarned(20)
        .academicPerformance(75f)
        .progressVisualization(
            ProgressVisualization.builder()
                .progressBarPercent(40f)
                .xpDisplay(20)
                .academicStatus("INITIAL")
                .statusColor("yellow")
                .tasksCompletedLabel("1/4 tasks")
                .build())
        .partialData(true)
        .build();
  }
}
