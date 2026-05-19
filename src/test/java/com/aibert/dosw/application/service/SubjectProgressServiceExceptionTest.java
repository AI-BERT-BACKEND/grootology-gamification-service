package com.aibert.dosw.application.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.aibert.dosw.application.dto.request.SubjectProgressDataDTO;
import com.aibert.dosw.application.dto.request.SubjectProgressBatchRequestDTO;
import com.aibert.dosw.application.mapper.SubjectProgressApplicationMapper;
import com.aibert.dosw.domain.exceptions.SubjectProgressNotFoundException;
import com.aibert.dosw.domain.exceptions.SubjectProgressLoadException;
import com.aibert.dosw.domain.ports.out.GamificationRepositoryPort;
import com.aibert.dosw.domain.ports.out.SubjectProgressRepositoryPort;
import com.aibert.dosw.infrastructure.feign.AcademicServiceClient;
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
class SubjectProgressServiceExceptionTest {

  @Mock private GamificationRepositoryPort gamificationRepository;
  @Mock private SubjectProgressRepositoryPort subjectProgressRepository;
  @Mock private AcademicServiceClient academicServiceClient;
  @Spy private SubjectProgressApplicationMapper mapper = Mappers.getMapper(SubjectProgressApplicationMapper.class);
  @InjectMocks private SubjectProgressService service;

  @Test
  void getProgressOverview_failure_fa03() {
    when(subjectProgressRepository.findByUserId(any())).thenThrow(new RuntimeException("db"));

    assertThrows(
        SubjectProgressLoadException.class, () -> service.getProgressOverview(UUID.randomUUID()));
  }

  @Test
  void updateProgress_runtimeFailure_wrapsAsFa03() {
    doThrow(new RuntimeException("mapping failure"))
        .when(mapper)
        .toDomain(any(SubjectProgressDataDTO.class));

    SubjectProgressBatchRequestDTO request = new SubjectProgressBatchRequestDTO();
    SubjectProgressDataDTO subject = new SubjectProgressDataDTO();
    subject.setSubjectId("math-101");
    subject.setAcademicPerformance(90f);
    request.setSubjects(List.of(subject));

    assertThrows(
        SubjectProgressLoadException.class,
        () -> service.updateProgress(UUID.randomUUID(), request));
  }

  @Test
  void getSubjectProgress_runtimeFailure_wrapsAsFa03() {
    when(subjectProgressRepository.findByUserIdAndSubjectId(any(), any()))
        .thenThrow(new RuntimeException("db"));

    assertThrows(
        SubjectProgressLoadException.class,
        () -> service.getSubjectProgress(UUID.randomUUID(), "math-101"));
  }

  @Test
  void getSubjectProgress_notFound_propagatesFa04() {
    when(subjectProgressRepository.findByUserIdAndSubjectId(any(), any())).thenReturn(Optional.empty());

    assertThrows(
        SubjectProgressNotFoundException.class,
        () -> service.getSubjectProgress(UUID.randomUUID(), "missing"));
  }

}
