package com.aibert.dosw.application.service;

import com.aibert.dosw.application.dto.request.SubjectProgressBatchRequestDTO;
import com.aibert.dosw.application.dto.request.SubjectProgressDataDTO;
import com.aibert.dosw.application.dto.response.SubjectProgressItemDTO;
import com.aibert.dosw.application.dto.response.SubjectProgressOverviewDTO;
import com.aibert.dosw.application.mapper.SubjectProgressApplicationMapper;
import com.aibert.dosw.domain.exceptions.NoSubjectsRegisteredException;
import com.aibert.dosw.domain.exceptions.SubjectProgressLoadException;
import com.aibert.dosw.domain.exceptions.SubjectProgressNotFoundException;
import com.aibert.dosw.domain.model.subject.SubjectProgressResult;
import com.aibert.dosw.domain.model.subject.SubjectProgressSnapshot;
import com.aibert.dosw.domain.model.user.GamificationProfile;
import com.aibert.dosw.domain.model.user.Level;
import com.aibert.dosw.domain.ports.in.SubjectProgressUseCase;
import com.aibert.dosw.domain.ports.out.GamificationRepositoryPort;
import com.aibert.dosw.domain.ports.out.SubjectProgressRepositoryPort;
import com.aibert.dosw.domain.service.GlobalLevelCalculator;
import com.aibert.dosw.domain.service.SubjectProgressProcessor;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubjectProgressService implements SubjectProgressUseCase {

  private final GamificationRepositoryPort gamificationRepository;
  private final SubjectProgressRepositoryPort subjectProgressRepository;
  private final SubjectProgressApplicationMapper subjectProgressMapper;
  private final SubjectProgressProcessor processor = new SubjectProgressProcessor();

  @Override
  public SubjectProgressOverviewDTO updateProgress(
      UUID userId, SubjectProgressBatchRequestDTO request) {
    try {
      if (request.getSubjects() == null || request.getSubjects().isEmpty()) {
        throw new NoSubjectsRegisteredException();
      }

      GamificationProfile profile = resolveProfile(userId);
      String username = resolveUsername(profile, userId);
      List<SubjectProgressItemDTO> items = new ArrayList<>();

      for (SubjectProgressDataDTO subjectData : request.getSubjects()) {
        SubjectProgressResult result = processor.calculate(subjectProgressMapper.toDomain(subjectData));
        items.add(subjectProgressMapper.toItemDto(result));
        if (result.isValid()) {
          subjectProgressRepository.save(toSnapshot(userId, result));
        }
      }

      return SubjectProgressOverviewDTO.builder()
          .userName(username)
          .userGlobalLevel(GlobalLevelCalculator.fromTotalXp(profile.getTotalPoints()))
          .totalGlobalXp(profile.getTotalPoints())
          .subjects(items)
          .build();
    } catch (NoSubjectsRegisteredException ex) {
      throw ex;
    } catch (RuntimeException ex) {
      throw new SubjectProgressLoadException(
          "Unable to load subject progress indicators (FA-03).", ex);
    }
  }

  @Override
  public SubjectProgressOverviewDTO getProgressOverview(UUID userId) {
    try {
      List<SubjectProgressSnapshot> snapshots = subjectProgressRepository.findByUserId(userId);
      if (snapshots.isEmpty()) {
        throw new NoSubjectsRegisteredException();
      }

      GamificationProfile profile = resolveProfile(userId);
      String username = resolveUsername(profile, userId);
      return SubjectProgressOverviewDTO.builder()
          .userName(username)
          .userGlobalLevel(profile.getGlobalLevel())
          .totalGlobalXp(profile.getTotalPoints())
          .subjects(snapshots.stream().map(subjectProgressMapper::snapshotToItem).toList())
          .build();
    } catch (NoSubjectsRegisteredException ex) {
      throw ex;
    } catch (RuntimeException ex) {
      throw new SubjectProgressLoadException(
          "Unable to load subject progress indicators (FA-03).", ex);
    }
  }

  @Override
  public SubjectProgressItemDTO getSubjectProgress(UUID userId, String subjectId) {
    try {
      SubjectProgressSnapshot snapshot =
          subjectProgressRepository
              .findByUserIdAndSubjectId(userId, subjectId)
              .orElseThrow(SubjectProgressNotFoundException::new);
      return subjectProgressMapper.snapshotToItem(snapshot);
    } catch (SubjectProgressNotFoundException ex) {
      throw ex;
    } catch (RuntimeException ex) {
      throw new SubjectProgressLoadException(
          "Unable to load subject progress indicators (FA-03).", ex);
    }
  }

  private GamificationProfile resolveProfile(UUID userId) {
    return gamificationRepository
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

  private SubjectProgressSnapshot toSnapshot(UUID userId, SubjectProgressResult result) {
    return SubjectProgressSnapshot.builder()
        .userId(userId)
        .subjectId(result.getSubjectId())
        .subjectName(result.getSubjectName())
        .subjectProgressPercentage(result.getSubjectProgressPercentage())
        .subjectLevel(result.getSubjectLevel())
        .xpEarned(result.getXpEarned())
        .academicPerformance(result.getAcademicPerformance())
        .progressVisualization(result.getProgressVisualization())
        .partialData(result.isPartialData())
        .build();
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
