package com.aibert.dosw.application.service;

import com.aibert.dosw.application.dto.request.AchievementUnlockRequestDTO;
import com.aibert.dosw.application.dto.response.AchievementGalleryItemDTO;
import com.aibert.dosw.application.dto.response.AchievementResponseDTO;
import com.aibert.dosw.application.mapper.AchievementApplicationMapper;
import com.aibert.dosw.application.mapper.ProgressDataMapper;
import com.aibert.dosw.domain.exceptions.AchievementUpdateException;
import com.aibert.dosw.domain.exceptions.GamificationProfileNotFoundException;
import com.aibert.dosw.domain.model.achievement.AchievementDefinition;
import com.aibert.dosw.domain.model.achievement.UnlockedAchievement;
import com.aibert.dosw.domain.model.user.GamificationProfile;
import com.aibert.dosw.domain.model.user.Level;
import com.aibert.dosw.domain.ports.in.AchievementUseCase;
import com.aibert.dosw.domain.ports.out.GamificationRepositoryPort;
import com.aibert.dosw.domain.service.AchievementSystemProcessor;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AchievementService implements AchievementUseCase {

  private final GamificationRepositoryPort repository;
  private final ProgressDataMapper progressDataMapper;
  private final AchievementApplicationMapper achievementMapper;
  private final AchievementSystemProcessor achievementProcessor = new AchievementSystemProcessor();

  @Override
  public AchievementResponseDTO unlockAchievement(UUID userId, AchievementUnlockRequestDTO request) {
    try {
      GamificationProfile profile = loadOrCreateProfile(userId);
      List<UnlockedAchievement> current =
          profile.getAchievements() != null ? profile.getAchievements() : new ArrayList<>();

      AchievementSystemProcessor.AchievementUnlockResult result =
          achievementProcessor.process(
              request.getAchievementEvent(),
              progressDataMapper.toDomainList(request.getUserProgressData()),
              request.getUnlockDate(),
              current);

      List<AchievementGalleryItemDTO> gallery =
          achievementMapper.toGalleryDto(achievementProcessor.buildGallery(result.getUpdatedAchievements()));
      List<AchievementGalleryItemDTO> recent =
          achievementMapper.toRecentDto(
              achievementProcessor.recentAchievements(result.getUpdatedAchievements()));

      if (!result.isAchievementUnlocked()) {
        return achievementMapper.toUnlockResponse(
            false, result.getUnlockedDefinition(), gallery, recent, result.getMessage());
      }

      repository.save(
          GamificationProfile.builder()
              .id(profile.getId())
              .userId(userId)
              .totalPoints(profile.getTotalPoints())
              .currentStreak(profile.getCurrentStreak())
              .lastActivityDate(profile.getLastActivityDate())
              .globalLevel(profile.getGlobalLevel())
              .achievements(result.getUpdatedAchievements())
              .build());

      AchievementDefinition unlocked = result.getUnlockedDefinition();
      return achievementMapper.toUnlockResponse(
          true,
          unlocked,
          gallery,
          recent,
          "Achievement unlocked: " + unlocked.getName());
    } catch (AchievementUpdateException ex) {
      throw ex;
    } catch (RuntimeException ex) {
      throw new AchievementUpdateException(
          "Unable to update student achievements. Please try again (FA-03).", ex);
    }
  }

  @Override
  public AchievementResponseDTO getGallery(UUID userId) {
    GamificationProfile profile =
        repository.findByUserId(userId).orElseThrow(GamificationProfileNotFoundException::new);

    List<UnlockedAchievement> achievements =
        profile.getAchievements() != null ? profile.getAchievements() : List.of();

    return achievementMapper.toUnlockResponse(
        false,
        null,
        achievementMapper.toGalleryDto(achievementProcessor.buildGallery(achievements)),
        achievementMapper.toRecentDto(achievementProcessor.recentAchievements(achievements)),
        null);
  }

  private GamificationProfile loadOrCreateProfile(UUID userId) {
    return repository
        .findByUserId(userId)
        .orElse(
            GamificationProfile.builder()
                .userId(userId)
                .totalPoints(0)
                .currentStreak(0)
                .globalLevel(Level.NOVATO)
                .achievements(new ArrayList<>())
                .build());
  }
}
