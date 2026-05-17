package com.aibert.dosw.application.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.aibert.dosw.application.dto.request.AchievementUnlockRequestDTO;
import com.aibert.dosw.application.mapper.AchievementApplicationMapper;
import com.aibert.dosw.application.mapper.ProgressDataMapper;
import com.aibert.dosw.domain.exceptions.AchievementUpdateException;
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
class AchievementServiceExceptionTest {

  @Mock private GamificationRepositoryPort repository;
  @Spy private ProgressDataMapper progressDataMapper = Mappers.getMapper(ProgressDataMapper.class);
  @Spy private AchievementApplicationMapper achievementMapper =
      Mappers.getMapper(AchievementApplicationMapper.class);
  @InjectMocks private AchievementService achievementService;

  @Test
  void unlockAchievement_failure_wrapsAsFa03() {
    when(repository.findByUserId(any())).thenThrow(new RuntimeException("db"));
    assertThrows(
        AchievementUpdateException.class,
        () -> achievementService.unlockAchievement(UUID.randomUUID(), new AchievementUnlockRequestDTO()));
  }
}
