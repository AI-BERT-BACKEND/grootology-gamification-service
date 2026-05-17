package com.aibert.dosw.domain.service;

import com.aibert.dosw.domain.model.achievement.AchievementEvent;
import com.aibert.dosw.domain.model.achievement.UnlockedAchievement;
import com.aibert.dosw.domain.model.achievement.UserProgressRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AchievementSystemProcessorTest {

  private AchievementSystemProcessor processor;
  private List<UnlockedAchievement> unlocked;

  @BeforeEach
  void setUp() {
    processor = new AchievementSystemProcessor();
    unlocked = new ArrayList<>();
  }

  @Test
  void process_perfectScore_unlocksAchievement() {
    var result =
        processor.process(
            AchievementEvent.PERFECT_SCORE,
            List.of(
                UserProgressRecord.builder()
                    .activityType("EXAM")
                    .completionDate(LocalDateTime.now())
                    .score(100)
                    .build()),
            LocalDateTime.now(),
            unlocked);

    assertTrue(result.isAchievementUnlocked());
    assertEquals("Puntuación Perfecta", result.getUnlockedDefinition().getName());
    assertEquals(1, result.getUpdatedAchievements().size());
  }

  @Test
  void process_duplicate_preventsReUnlock() {
    unlocked.add(
        UnlockedAchievement.builder()
            .event(AchievementEvent.GOAL_COMPLETED)
            .unlockDate(LocalDateTime.now().minusDays(1))
            .build());

    var result =
        processor.process(
            AchievementEvent.GOAL_COMPLETED,
            List.of(
                UserProgressRecord.builder()
                    .activityType("GOAL_COMPLETED")
                    .completionDate(LocalDateTime.now())
                    .build()),
            LocalDateTime.now(),
            unlocked);

    assertFalse(result.isAchievementUnlocked());
    assertTrue(result.getMessage().contains("FA-02"));
  }

  @Test
  void process_conditionsNotMet_fa01() {
    var result =
        processor.process(
            AchievementEvent.TASK_STREAK,
            List.of(
                UserProgressRecord.builder()
                    .activityType("TASK")
                    .completionDate(LocalDateTime.now())
                    .streakDays(3)
                    .build()),
            LocalDateTime.now(),
            unlocked);

    assertFalse(result.isAchievementUnlocked());
    assertTrue(result.getMessage().contains("FA-01"));
  }

  @Test
  void process_emptyProgress_fa01() {
    var result =
        processor.process(
            AchievementEvent.SUBJECT_MASTERY, List.of(), LocalDateTime.now(), unlocked);

    assertFalse(result.isAchievementUnlocked());
  }

  @Test
  void buildGallery_includesLockedAndUnlocked() {
    unlocked.add(
        UnlockedAchievement.builder()
            .event(AchievementEvent.PERFECT_SCORE)
            .unlockDate(LocalDateTime.now())
            .build());

    var gallery = processor.buildGallery(unlocked);

    assertEquals(5, gallery.size());
    assertEquals(1, gallery.stream().filter(AchievementSystemProcessor.AchievementGalleryEntry::isUnlocked).count());
    assertEquals(4, gallery.stream().filter(e -> !e.isUnlocked()).count());
  }

  @Test
  void process_goalCompleted_unlocks() {
    var result =
        processor.process(
            AchievementEvent.GOAL_COMPLETED,
            List.of(
                UserProgressRecord.builder()
                    .activityType("GOAL_COMPLETED")
                    .completionDate(LocalDateTime.now())
                    .build()),
            LocalDateTime.now(),
            unlocked);
    assertTrue(result.isAchievementUnlocked());
  }

  @Test
  void process_subjectMastery_unlocks() {
    var result =
        processor.process(
            AchievementEvent.SUBJECT_MASTERY,
            List.of(
                UserProgressRecord.builder().progressPercent(100).completionDate(LocalDateTime.now()).build()),
            LocalDateTime.now(),
            unlocked);
    assertTrue(result.isAchievementUnlocked());
  }

  @Test
  void process_productivityStreak_unlocks() {
    var result =
        processor.process(
            AchievementEvent.PRODUCTIVITY_STREAK,
            List.of(UserProgressRecord.builder().streakDays(14).completionDate(LocalDateTime.now()).build()),
            LocalDateTime.now(),
            unlocked);
    assertTrue(result.isAchievementUnlocked());
  }

  @Test
  void process_taskStreak_unlocksWith7Days() {
    var result =
        processor.process(
            AchievementEvent.TASK_STREAK,
            List.of(
                UserProgressRecord.builder()
                    .activityType("TASK")
                    .streakDays(7)
                    .completionDate(LocalDateTime.now())
                    .build()),
            LocalDateTime.now(),
            unlocked);

    assertTrue(result.isAchievementUnlocked());
    assertEquals("Racha de Tareas", result.getUnlockedDefinition().getName());
  }
}
