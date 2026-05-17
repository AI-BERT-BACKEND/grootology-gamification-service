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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
      String username = resolveUsername(profile, userId);
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
            username, false, result.getUnlockedDefinition(), gallery, recent, result.getMessage());
      }

      repository.save(
          GamificationProfile.builder()
              .id(profile.getId())
              .userId(userId)
              .username(username)
              .totalPoints(profile.getTotalPoints())
              .currentStreak(profile.getCurrentStreak())
              .lastActivityDate(profile.getLastActivityDate())
              .globalLevel(profile.getGlobalLevel())
              .achievements(result.getUpdatedAchievements())
              .build());

      AchievementDefinition unlocked = result.getUnlockedDefinition();
      return achievementMapper.toUnlockResponse(
          username,
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
    String username = resolveUsername(profile, userId);

    List<UnlockedAchievement> achievements =
        profile.getAchievements() != null ? profile.getAchievements() : List.of();

    return achievementMapper.toUnlockResponse(
        username,
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
                .username(resolveUsername(null, userId))
                .totalPoints(0)
                .currentStreak(0)
                .globalLevel(Level.NOVATO)
                .achievements(new ArrayList<>())
                .build());
  }

  private String resolveUsername(GamificationProfile profile, UUID userId) {
    if (profile != null && profile.getUsername() != null && !profile.getUsername().isBlank()) {
      return profile.getUsername();
    }

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null) {
      String principal = String.valueOf(authentication.getPrincipal());
      if (!principal.isBlank() && !"anonymousUser".equalsIgnoreCase(principal)) {
        return principal;
      }
    }

    return userId.toString();
  }
}
