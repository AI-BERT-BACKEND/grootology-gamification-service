package com.aibert.dosw.application.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.aibert.dosw.application.dto.request.ActionEventRequestDTO;
import com.aibert.dosw.application.mapper.ActivityHistoryMapper;
import com.aibert.dosw.application.mapper.PointsApplicationMapper;
import com.aibert.dosw.domain.exceptions.PointsUpdateException;
import com.aibert.dosw.domain.ports.out.GamificationRepositoryPort;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PointsServiceExceptionTest {

  @Mock private GamificationRepositoryPort repository;
  @Spy private ActivityHistoryMapper activityHistoryMapper = Mappers.getMapper(ActivityHistoryMapper.class);
  @Spy private PointsApplicationMapper pointsMapper = Mappers.getMapper(PointsApplicationMapper.class);
  @InjectMocks private PointsService pointsService;

  @Test
  void processAcademicEvent_repositoryFailure_wrapsException() {
    ActionEventRequestDTO request = new ActionEventRequestDTO();
    when(repository.findByUserId(any())).thenThrow(new RuntimeException("db down"));

    assertThrows(
        PointsUpdateException.class,
        () -> pointsService.processAcademicEvent(UUID.randomUUID(), request));
  }
}
