package com.aibert.dosw.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.aibert.dosw.application.dto.response.GamificationProgressResponseDTO;
import com.aibert.dosw.domain.exceptions.GamificationProgressLoadException;
import com.aibert.dosw.domain.model.achievement.AchievementEvent;
import com.aibert.dosw.domain.model.achievement.UnlockedAchievement;
import com.aibert.dosw.domain.model.user.GamificationProfile;
import com.aibert.dosw.domain.model.user.Level;
import com.aibert.dosw.domain.ports.out.GamificationRepositoryPort;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GamificationProgressServiceTest {

  @Mock private GamificationRepositoryPort repository;

  @InjectMocks private GamificationProgressService service;

  @Test
  void getGamificationProgress_existingProfile_returnsExpectedSummary() {
    UUID userId = UUID.randomUUID();
    when(repository.findByUserId(userId))
        .thenReturn(
            Optional.of(
                GamificationProfile.builder()
                    .userId(userId)
                    .username("student.progress")
                    .totalPoints(250)
                    .currentStreak(3)
                    .globalLevel(Level.COMPROMETIDO)
                    .achievements(
                        List.of(
                            UnlockedAchievement.builder()
                                .event(AchievementEvent.PERFECT_SCORE)
                                .unlockDate(LocalDateTime.of(2026, 5, 17, 10, 0))
                                .build()))
                    .build()));

    GamificationProgressResponseDTO response = service.getGamificationProgress(userId);

    assertEquals(250, response.getTotalPoints());
    assertEquals(2, response.getCurrentLevel());
    assertEquals(75f, response.getProgressToNext());
    assertEquals(5, response.getBadges().size());

    var unlockedBadge =
        response.getBadges().stream().filter(b -> b.isUnlocked()).findFirst().orElseThrow();
    assertTrue(unlockedBadge.isUnlocked());
    assertNotNull(unlockedBadge.getUnlockedDate());
    assertNotNull(unlockedBadge.getBadgeId());
  }

  @Test
  void getGamificationProgress_noActivity_returnsInitialState() {
    UUID userId = UUID.randomUUID();
    when(repository.findByUserId(userId)).thenReturn(Optional.empty());

    GamificationProgressResponseDTO response = service.getGamificationProgress(userId);

    assertEquals(0, response.getTotalPoints());
    assertEquals(1, response.getCurrentLevel());
    assertEquals(0f, response.getProgressToNext());
    assertEquals(5, response.getBadges().size());
    assertTrue(response.getBadges().stream().noneMatch(b -> b.isUnlocked()));
  }

  @Test
  void getGamificationProgress_repositoryFailure_wrapsException() {
    when(repository.findByUserId(org.mockito.ArgumentMatchers.any()))
        .thenThrow(new RuntimeException("db down"));

    assertThrows(
        GamificationProgressLoadException.class,
        () -> service.getGamificationProgress(UUID.randomUUID()));
  }

  @Test
  void getGamificationProgress_maxLevel_reports100Progress() {
    UUID userId = UUID.randomUUID();
    when(repository.findByUserId(userId))
        .thenReturn(
            Optional.of(
                GamificationProfile.builder()
                    .userId(userId)
                    .totalPoints(720)
                    .globalLevel(Level.MAESTRO_DEL_TIEMPO)
                    .achievements(List.of())
                    .build()));

    GamificationProgressResponseDTO response = service.getGamificationProgress(userId);

    assertEquals(4, response.getCurrentLevel());
    assertEquals(100f, response.getProgressToNext());
    assertFalse(response.getBadges().isEmpty());
  }
}
