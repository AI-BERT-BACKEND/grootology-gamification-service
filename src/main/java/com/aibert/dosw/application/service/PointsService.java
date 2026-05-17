package com.aibert.dosw.application.service;

import com.aibert.dosw.application.dto.request.ActionEventRequestDTO;
import com.aibert.dosw.application.dto.response.PointsResponseDTO;
import com.aibert.dosw.application.mapper.ActivityHistoryMapper;
import com.aibert.dosw.application.mapper.PointsApplicationMapper;
import com.aibert.dosw.domain.exceptions.GamificationProfileNotFoundException;
import com.aibert.dosw.domain.exceptions.PointsUpdateException;
import com.aibert.dosw.domain.model.user.GamificationProfile;
import com.aibert.dosw.domain.model.user.Level;
import com.aibert.dosw.domain.ports.in.PointsUseCase;
import com.aibert.dosw.domain.ports.out.GamificationRepositoryPort;
import com.aibert.dosw.domain.service.GlobalLevelCalculator;
import com.aibert.dosw.domain.service.PointsSystemProcessor;
import java.util.ArrayList;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointsService implements PointsUseCase {

  private final GamificationRepositoryPort repository;
  private final ActivityHistoryMapper activityHistoryMapper;
  private final PointsApplicationMapper pointsMapper;
  private final PointsSystemProcessor pointsProcessor = new PointsSystemProcessor();

  @Override
  public PointsResponseDTO processAcademicEvent(UUID userId, ActionEventRequestDTO request) {
    try {
      GamificationProfile profile = loadOrCreateProfile(userId);
      String username = resolveUsername(profile, userId);

      PointsSystemProcessor.PointsAwardResult award =
          pointsProcessor.process(
              profile,
              request.getActionEvent(),
              request.getCompletionDate(),
              request.getDueDate(),
              request.getActivityId(),
              activityHistoryMapper.toDomainList(request.getUserActivityHistory()));

      if (!award.isPointsUpdated()) {
        return pointsMapper.toResponse(award);
      }

      GamificationProfile updated =
          GamificationProfile.builder()
              .id(profile.getId())
              .userId(userId)
              .username(username)
              .totalPoints(award.getTotalPoints())
              .currentStreak(award.getCurrentStreak())
              .lastActivityDate(award.getLastActivityDate())
              .globalLevel(GlobalLevelCalculator.fromTotalXp(award.getTotalPoints()))
              .achievements(
                  profile.getAchievements() != null
                      ? profile.getAchievements()
                      : new ArrayList<>())
              .build();

      repository.save(updated);
      return pointsMapper.toResponse(award);
    } catch (PointsUpdateException ex) {
      throw ex;
    } catch (RuntimeException ex) {
      throw new PointsUpdateException(
          "Unable to update student points. Please try again (FA-05).", ex);
    }
  }

  @Override
  public PointsResponseDTO getPointsSummary(UUID userId) {
    GamificationProfile profile =
        repository.findByUserId(userId).orElseThrow(GamificationProfileNotFoundException::new);
    return pointsMapper.toQueryResponse(profile);
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
