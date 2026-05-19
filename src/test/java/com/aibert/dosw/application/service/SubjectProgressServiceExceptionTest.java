package com.aibert.dosw.application.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.aibert.dosw.application.dto.request.SubjectProgressBatchRequestDTO;
import com.aibert.dosw.application.mapper.SubjectProgressApplicationMapper;
import com.aibert.dosw.domain.exceptions.SubjectProgressLoadException;
import com.aibert.dosw.domain.ports.out.GamificationRepositoryPort;
import com.aibert.dosw.domain.ports.out.SubjectProgressRepositoryPort;
import com.aibert.dosw.infrastructure.feign.AcademicServiceClient;
import java.util.List;
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

}
