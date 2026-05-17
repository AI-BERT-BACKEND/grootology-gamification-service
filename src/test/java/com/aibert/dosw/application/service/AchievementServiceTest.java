package com.aibert.dosw.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.aibert.dosw.application.dto.request.AchievementUnlockRequestDTO;
import com.aibert.dosw.application.dto.request.ActivityRecordDTO;
import com.aibert.dosw.application.dto.response.AchievementResponseDTO;
import com.aibert.dosw.application.mapper.AchievementApplicationMapper;
import com.aibert.dosw.application.mapper.ProgressDataMapper;
import com.aibert.dosw.domain.exceptions.GamificationProfileNotFoundException;
import com.aibert.dosw.domain.model.achievement.AchievementEvent;
import com.aibert.dosw.domain.model.achievement.UnlockedAchievement;
import com.aibert.dosw.domain.model.user.GamificationProfile;
import com.aibert.dosw.domain.model.user.Level;
import com.aibert.dosw.domain.ports.out.GamificationRepositoryPort;
import java.time.LocalDateTime;
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
class AchievementServiceTest {

  @Mock private GamificationRepositoryPort repository;
  @Spy private ProgressDataMapper progressDataMapper = Mappers.getMapper(ProgressDataMapper.class);
  @Spy private AchievementApplicationMapper achievementMapper =
      Mappers.getMapper(AchievementApplicationMapper.class);
  @InjectMocks private AchievementService achievementService;

  private final UUID userId = UUID.randomUUID();

  @Test
  void unlockAchievement_success() {
    when(repository.findByUserId(userId)).thenReturn(Optional.empty());
    when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    AchievementResponseDTO response =
        achievementService.unlockAchievement(userId, buildRequest(AchievementEvent.PERFECT_SCORE, 100));

    assertTrue(response.isAchievementUnlocked());
    assertEquals(userId.toString(), response.getUsername());
    assertNotNull(response.getAchievementBadge());
    assertEquals(5, response.getAchievementGallery().size());
    verify(repository).save(any());
  }

  @Test
  void unlockAchievement_duplicate_doesNotSave() {
    GamificationProfile profile =
        GamificationProfile.builder()
            .userId(userId)
            .username("student.test")
            .totalPoints(50)
            .currentStreak(2)
            .globalLevel(Level.NOVATO)
            .achievements(
                List.of(
                    UnlockedAchievement.builder()
                        .event(AchievementEvent.PERFECT_SCORE)
                        .unlockDate(LocalDateTime.now().minusDays(1))
                        .build()))
            .build();

    when(repository.findByUserId(userId)).thenReturn(Optional.of(profile));

    AchievementResponseDTO response =
        achievementService.unlockAchievement(userId, buildRequest(AchievementEvent.PERFECT_SCORE, 100));

    assertFalse(response.isAchievementUnlocked());
    assertEquals("student.test", response.getUsername());
    verify(repository, never()).save(any());
  }

  @Test
  void getGallery_profileNotFound() {
    when(repository.findByUserId(userId)).thenReturn(Optional.empty());
    assertThrows(GamificationProfileNotFoundException.class, () -> achievementService.getGallery(userId));
  }

  private AchievementUnlockRequestDTO buildRequest(AchievementEvent event, int score) {
    ActivityRecordDTO record = new ActivityRecordDTO();
    record.setActivityType("EXAM");
    record.setCompletionDate(LocalDateTime.of(2026, 5, 17, 10, 0));
    record.setScore(score);

    AchievementUnlockRequestDTO request = new AchievementUnlockRequestDTO();
    request.setAchievementEvent(event);
    request.setUserProgressData(List.of(record));
    return request;
  }
}
