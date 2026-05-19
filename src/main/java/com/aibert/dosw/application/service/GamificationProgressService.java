package com.aibert.dosw.application.service;

import com.aibert.dosw.application.dto.response.GamificationProgressBadgeDTO;
import com.aibert.dosw.application.dto.response.GamificationProgressResponseDTO;
import com.aibert.dosw.domain.exceptions.GamificationProgressLoadException;
import com.aibert.dosw.domain.model.achievement.AchievementCatalog;
import com.aibert.dosw.domain.model.achievement.AchievementDefinition;
import com.aibert.dosw.domain.model.achievement.AchievementEvent;
import com.aibert.dosw.domain.model.achievement.UnlockedAchievement;
import com.aibert.dosw.domain.model.user.GamificationProfile;
import com.aibert.dosw.domain.ports.in.GamificationProgressUseCase;
import com.aibert.dosw.domain.ports.out.GamificationRepositoryPort;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GamificationProgressService implements GamificationProgressUseCase {

  private static final int LEVEL_2_MIN = 100;
  private static final int LEVEL_3_MIN = 300;
  private static final int LEVEL_4_MIN = 600;

  private final GamificationRepositoryPort repository;

  @Override
  public GamificationProgressResponseDTO getGamificationProgress(UUID userId) {
    try {
      GamificationProfile profile = repository.findByUserId(userId).orElse(null);
      int totalPoints = profile != null ? Math.max(profile.getTotalPoints(), 0) : 0;
      int currentLevel = resolveCurrentLevel(totalPoints);
      float progressToNext = resolveProgressToNext(totalPoints, currentLevel);
      List<UnlockedAchievement> unlocked =
          profile != null && profile.getAchievements() != null ? profile.getAchievements() : List.of();

      return GamificationProgressResponseDTO.builder()
          .totalPoints(totalPoints)
          .currentLevel(currentLevel)
          .badges(buildBadges(unlocked))
          .progressToNext(progressToNext)
          .build();
    } catch (GamificationProgressLoadException ex) {
      throw ex;
    } catch (RuntimeException ex) {
      throw new GamificationProgressLoadException(
          "Unable to load academic gamification progress (FA-01).", ex);
    }
  }

  private List<GamificationProgressBadgeDTO> buildBadges(List<UnlockedAchievement> unlocked) {
    Map<AchievementEvent, UnlockedAchievement> unlockedByEvent =
        unlocked.stream().collect(Collectors.toMap(UnlockedAchievement::getEvent, Function.identity()));

    return AchievementCatalog.all().stream()
        .map(def -> toBadge(def, unlockedByEvent.get(def.getEvent())))
        .toList();
  }

  private GamificationProgressBadgeDTO toBadge(
      AchievementDefinition definition, UnlockedAchievement unlocked) {
    return GamificationProgressBadgeDTO.builder()
        .badgeId(badgeIdFor(definition.getEvent()))
        .badgeName(definition.getName())
        .icon(definition.getIcon())
        .description(definition.getDescription())
        .unlocked(unlocked != null)
        .unlockedDate(resolveUnlockedDate(unlocked))
        .build();
  }

  private UUID badgeIdFor(AchievementEvent event) {
    return UUID.nameUUIDFromBytes(("AIB-35-BADGE-" + event.name()).getBytes(StandardCharsets.UTF_8));
  }

  private LocalDate resolveUnlockedDate(UnlockedAchievement unlocked) {
    if (unlocked == null || unlocked.getUnlockDate() == null) {
      return null;
    }
    return unlocked.getUnlockDate().toLocalDate();
  }

  private int resolveCurrentLevel(int totalPoints) {
    if (totalPoints >= LEVEL_4_MIN) {
      return 4;
    }
    if (totalPoints >= LEVEL_3_MIN) {
      return 3;
    }
    if (totalPoints >= LEVEL_2_MIN) {
      return 2;
    }
    return 1;
  }

  private float resolveProgressToNext(int totalPoints, int currentLevel) {
    return switch (currentLevel) {
      case 1 -> round2(((float) totalPoints / LEVEL_2_MIN) * 100f);
      case 2 -> round2(((float) (totalPoints - LEVEL_2_MIN) / (LEVEL_3_MIN - LEVEL_2_MIN)) * 100f);
      case 3 -> round2(((float) (totalPoints - LEVEL_3_MIN) / (LEVEL_4_MIN - LEVEL_3_MIN)) * 100f);
      default -> 100f;
    };
  }

  private float round2(float value) {
    float clamped = Math.max(0f, Math.min(value, 100f));
    return Math.round(clamped * 100f) / 100f;
  }
}
