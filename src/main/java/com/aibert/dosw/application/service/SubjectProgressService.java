package com.aibert.dosw.application.service;

import com.aibert.dosw.application.dto.request.SubjectProgressBatchRequestDTO;
import com.aibert.dosw.application.dto.request.CompletedTaskDTO;
import com.aibert.dosw.application.dto.request.SubjectProgressDataDTO;
import com.aibert.dosw.application.dto.response.SubjectProgressItemDTO;
import com.aibert.dosw.application.dto.response.SubjectProgressOverviewDTO;
import com.aibert.dosw.application.mapper.SubjectProgressApplicationMapper;
import com.aibert.dosw.domain.exceptions.NoSubjectsRegisteredException;
import com.aibert.dosw.domain.exceptions.SubjectProgressLoadException;
import com.aibert.dosw.domain.exceptions.SubjectProgressNotFoundException;
import com.aibert.dosw.domain.model.subject.SubjectProgressResult;
import com.aibert.dosw.domain.model.subject.SubjectProgressRules;
import com.aibert.dosw.domain.model.subject.SubjectProgressSnapshot;
import com.aibert.dosw.domain.model.user.GamificationProfile;
import com.aibert.dosw.domain.model.user.Level;
import com.aibert.dosw.domain.ports.in.SubjectProgressUseCase;
import com.aibert.dosw.domain.ports.out.AcademicSummaryProviderPort;
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
  private final AcademicSummaryProviderPort academicSummaryProvider;
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
  public SubjectProgressOverviewDTO syncProgressFromAcademic(UUID userId, String studentId) {
    try {
      String resolvedStudentId =
          studentId == null || studentId.isBlank() ? userId.toString() : studentId;
      var academicSummary = academicSummaryProvider.fetchAcademicSummary(resolvedStudentId);

      if (academicSummary.subjects() == null || academicSummary.subjects().isEmpty()) {
        throw new NoSubjectsRegisteredException();
      }

      List<SubjectProgressDataDTO> subjects = new ArrayList<>();
      for (var subject : academicSummary.subjects()) {
        if (subject.subjectId() == null) {
          continue;
        }

        SubjectProgressDataDTO dto = new SubjectProgressDataDTO();
        dto.setSubjectId(subject.subjectId().toString());
        dto.setSubjectName(subject.subjectName());
        dto.setCompletedTasks(mapCompletedTasks(subject.cuts()));
        dto.setTotalTasks(subject.cuts() == null ? 0 : subject.cuts().size());
        dto.setAcademicPerformance(toPerformance(subject.overallAverage()));
        subjects.add(dto);
      }

      if (subjects.isEmpty()) {
        throw new NoSubjectsRegisteredException();
      }

      SubjectProgressBatchRequestDTO batch = new SubjectProgressBatchRequestDTO();
      batch.setSubjects(subjects);
      return updateProgress(userId, batch);
    } catch (NoSubjectsRegisteredException ex) {
      throw ex;
    } catch (RuntimeException ex) {
      throw new SubjectProgressLoadException(
          "Unable to synchronize subject progress with academic service (FA-03).", ex);
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

  private List<CompletedTaskDTO> mapCompletedTasks(
      List<AcademicSummaryProviderPort.EvaluationCut> cuts) {
    if (cuts == null || cuts.isEmpty()) {
      return List.of();
    }

    List<CompletedTaskDTO> completedTasks = new ArrayList<>();
    for (var cut : cuts) {
      if (cut == null || cut.grade() == null) {
        continue;
      }
      CompletedTaskDTO task = new CompletedTaskDTO();
      task.setTaskId(cut.id() == null ? "cut-" + cut.cutName() : "cut-" + cut.id());
      task.setTaskName(cut.cutName());
      task.setXpValue(SubjectProgressRules.XP_PER_COMPLETED_TASK);
      completedTasks.add(task);
    }
    return completedTasks;
  }

  private float toPerformance(Double overallAverage) {
    if (overallAverage == null) {
      return 0f;
    }
    float normalized = (float) (overallAverage * 20.0d);
    return Math.clamp(normalized, 0f, 100f);
  }
}
