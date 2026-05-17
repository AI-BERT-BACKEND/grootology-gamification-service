package com.aibert.dosw.domain.service;

import com.aibert.dosw.domain.model.achievement.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/** Lógica de negocio del Sistema de Logros (AIB-37). */
public class AchievementSystemProcessor {

  public AchievementUnlockResult process(
      AchievementEvent achievementEvent,
      List<UserProgressRecord> userProgressData,
      LocalDateTime unlockDate,
      List<UnlockedAchievement> currentAchievements) {

    if (!AchievementCatalog.isValidEvent(achievementEvent)) {
      return rejected(
          currentAchievements,
          "Event does not correspond to a valid achievement (FA-04).",
          null);
    }

    if (userProgressData == null || userProgressData.isEmpty()) {
      return rejected(
          currentAchievements,
          "Unlock conditions are not met (FA-01).",
          null);
    }

    Optional<AchievementDefinition> definition = AchievementCatalog.find(achievementEvent);
    if (definition.isEmpty()) {
      return rejected(currentAchievements, "Achievement not recognized by the system (FA-04).", null);
    }

    if (isAlreadyUnlocked(achievementEvent, currentAchievements)) {
      return rejected(
          currentAchievements,
          "Achievement was already unlocked (FA-02).",
          definition.get());
    }

    if (!meetsUnlockConditions(achievementEvent, userProgressData)) {
      return rejected(
          currentAchievements,
          "Unlock conditions are not met (FA-01).",
          definition.get());
    }

    LocalDateTime resolvedUnlockDate =
        unlockDate != null ? unlockDate : LocalDateTime.now();

    List<UnlockedAchievement> updatedAchievements = new ArrayList<>(currentAchievements);
    updatedAchievements.add(
        UnlockedAchievement.builder()
            .event(achievementEvent)
            .unlockDate(resolvedUnlockDate)
            .build());

    return AchievementUnlockResult.builder()
        .achievementUnlocked(true)
        .unlockedDefinition(definition.get())
        .unlockDate(resolvedUnlockDate)
        .updatedAchievements(updatedAchievements)
        .message(null)
        .build();
  }

  public List<AchievementGalleryEntry> buildGallery(List<UnlockedAchievement> unlocked) {
    return AchievementCatalog.all().stream()
        .map(
            def -> {
              Optional<UnlockedAchievement> match =
                  unlocked.stream().filter(a -> a.getEvent() == def.getEvent()).findFirst();
              return AchievementGalleryEntry.builder()
                  .definition(def)
                  .unlocked(match.isPresent())
                  .unlockDate(match.map(UnlockedAchievement::getUnlockDate).orElse(null))
                  .build();
            })
        .toList();
  }

  public List<UnlockedAchievement> recentAchievements(List<UnlockedAchievement> unlocked) {
    return unlocked.stream()
        .sorted(Comparator.comparing(UnlockedAchievement::getUnlockDate).reversed())
        .limit(AchievementConditions.RECENT_ACHIEVEMENTS_LIMIT)
        .toList();
  }

  private boolean isAlreadyUnlocked(
      AchievementEvent event, List<UnlockedAchievement> achievements) {
    if (achievements == null) {
      return false;
    }
    return achievements.stream().anyMatch(a -> a.getEvent() == event);
  }

  private boolean meetsUnlockConditions(
      AchievementEvent event, List<UserProgressRecord> progressData) {
    return switch (event) {
      case TASK_STREAK -> maxStreakDays(progressData) >= AchievementConditions.TASK_STREAK_REQUIRED_DAYS;
      case PERFECT_SCORE ->
          progressData.stream()
              .anyMatch(
                  r ->
                      r.getScore() != null
                          && r.getScore() >= AchievementConditions.PERFECT_SCORE_REQUIRED);
      case GOAL_COMPLETED ->
          progressData.stream()
              .anyMatch(
                  r ->
                      "GOAL_COMPLETED".equalsIgnoreCase(safe(r.getActivityType()))
                          || "WEEKLY_GOAL".equalsIgnoreCase(safe(r.getActivityType())));
      case SUBJECT_MASTERY ->
          progressData.stream()
              .anyMatch(
                  r ->
                      r.getProgressPercent() != null
                          && r.getProgressPercent()
                              >= AchievementConditions.SUBJECT_MASTERY_PROGRESS_PERCENT);
      case PRODUCTIVITY_STREAK ->
          maxStreakDays(progressData)
              >= AchievementConditions.PRODUCTIVITY_STREAK_REQUIRED_DAYS;
    };
  }

  private int maxStreakDays(List<UserProgressRecord> progressData) {
    return progressData.stream()
        .map(UserProgressRecord::getStreakDays)
        .filter(days -> days != null && days > 0)
        .max(Integer::compareTo)
        .orElse(0);
  }

  private String safe(String value) {
    return value == null ? "" : value.trim();
  }

  private AchievementUnlockResult rejected(
      List<UnlockedAchievement> current,
      String message,
      AchievementDefinition definition) {
    List<UnlockedAchievement> safeList = current == null ? List.of() : current;
    return AchievementUnlockResult.builder()
        .achievementUnlocked(false)
        .unlockedDefinition(definition)
        .unlockDate(null)
        .updatedAchievements(safeList)
        .message(message)
        .build();
  }

  @lombok.Getter
  @lombok.Builder
  public static class AchievementUnlockResult {
    private final boolean achievementUnlocked;
    private final AchievementDefinition unlockedDefinition;
    private final LocalDateTime unlockDate;
    private final List<UnlockedAchievement> updatedAchievements;
    private final String message;
  }

  @lombok.Getter
  @lombok.Builder
  public static class AchievementGalleryEntry {
    private final AchievementDefinition definition;
    private final boolean unlocked;
    private final LocalDateTime unlockDate;
  }
}
