package com.aibert.dosw.domain.service;

import com.aibert.dosw.domain.model.user.ActionEvent;
import com.aibert.dosw.domain.model.user.GamificationProfile;
import com.aibert.dosw.domain.model.user.Level;
import com.aibert.dosw.domain.model.user.UserActivityRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PointsSystemProcessorTest {

  private PointsSystemProcessor processor;
  private GamificationProfile profile;

  @BeforeEach
  void setUp() {
    processor = new PointsSystemProcessor();
    profile =
        GamificationProfile.builder()
            .userId(UUID.randomUUID())
            .totalPoints(0)
            .currentStreak(0)
            .globalLevel(Level.NOVATO)
            .achievements(new ArrayList<>())
            .build();
  }

  @Test
  void process_taskCompletedOnTime_awardsXpAndIncrementsStreak() {
    LocalDateTime completion = LocalDateTime.of(2026, 5, 17, 10, 0);
    LocalDateTime due = LocalDateTime.of(2026, 5, 17, 23, 59);

    var result =
        processor.process(
            profile,
            ActionEvent.TASK_COMPLETED,
            completion,
            due,
            "task-1",
            List.of(pastRecord(ActionEvent.SUBJECT_PROGRESS, completion.minusDays(1))));

    assertTrue(result.isPointsUpdated());
    assertEquals(15, result.getXpEarned());
    assertEquals(15, result.getTotalPoints());
    assertEquals(1, result.getCurrentStreak());
  }

  @Test
  void process_taskCompletedLate_noPunctualityBonus() {
    LocalDateTime completion = LocalDateTime.of(2026, 5, 18, 10, 0);
    LocalDateTime due = LocalDateTime.of(2026, 5, 17, 23, 59);

    var result =
        processor.process(
            profile,
            ActionEvent.TASK_COMPLETED,
            completion,
            due,
            "task-late",
            List.of(pastRecord(ActionEvent.SUBJECT_PROGRESS, completion.minusDays(1))));

    assertTrue(result.isPointsUpdated());
    assertEquals(10, result.getXpEarned());
  }

  @Test
  void process_duplicateEvent_doesNotAwardPoints() {
    LocalDateTime completion = LocalDateTime.of(2026, 5, 17, 10, 0);
    UserActivityRecord duplicate =
        UserActivityRecord.builder()
            .actionEvent(ActionEvent.TASK_COMPLETED)
            .completionDate(completion)
            .activityId("task-1")
            .build();

    var result =
        processor.process(
            profile,
            ActionEvent.TASK_COMPLETED,
            completion,
            null,
            "task-1",
            List.of(duplicate));

    assertFalse(result.isPointsUpdated());
    assertEquals(0, result.getXpEarned());
    assertTrue(result.getMessage().contains("FA-04"));
  }

  @Test
  void process_invalidRequest_emptyHistory() {
    var result =
        processor.process(
            profile,
            ActionEvent.TASK_COMPLETED,
            LocalDateTime.now(),
            null,
            "task-1",
            List.of());

    assertFalse(result.isPointsUpdated());
    assertTrue(result.getMessage().contains("FA-01"));
  }

  @Test
  void process_inactivity_resetsStreakBeforeIncrement() {
    profile =
        GamificationProfile.builder()
            .userId(UUID.randomUUID())
            .totalPoints(50)
            .currentStreak(5)
            .lastActivityDate(LocalDate.of(2026, 5, 10))
            .globalLevel(Level.CONSTANTE)
            .achievements(new ArrayList<>())
            .build();

    LocalDateTime completion = LocalDateTime.of(2026, 5, 17, 9, 0);
    var result =
        processor.process(
            profile,
            ActionEvent.TASK_COMPLETED,
            completion,
            completion.plusHours(8),
            "task-after-gap",
            List.of(pastRecord(ActionEvent.SUBJECT_PROGRESS, completion.minusDays(20))));

    assertTrue(result.isPointsUpdated());
    assertEquals(1, result.getCurrentStreak());
  }

  @Test
  void process_weeklyGoalAndStreak_awardXp() {
    LocalDateTime completion = LocalDateTime.of(2026, 5, 17, 18, 0);

    var weekly =
        processor.process(
            profile,
            ActionEvent.WEEKLY_GOAL_COMPLETED,
            completion,
            null,
            "goal-1",
            List.of(pastRecord(ActionEvent.TASK_COMPLETED, completion.minusDays(1))));

    var streak =
        processor.process(
            profile,
            ActionEvent.STREAK_COMPLETED,
            completion,
            null,
            "streak-1",
            List.of(pastRecord(ActionEvent.TASK_COMPLETED, completion.minusDays(1))));

    assertEquals(25, weekly.getXpEarned());
    assertEquals(20, streak.getXpEarned());
  }

  @Test
  void process_weeklyGoal_awards25Xp() {
    LocalDateTime completion = LocalDateTime.of(2026, 5, 17, 12, 0);
    var result =
        processor.process(
            profile,
            ActionEvent.WEEKLY_GOAL_COMPLETED,
            completion,
            null,
            "goal-1",
            List.of(pastRecord(ActionEvent.TASK_COMPLETED, completion.minusDays(2))));

    assertEquals(25, result.getXpEarned());
    assertTrue(result.isPointsUpdated());
  }

  @Test
  void process_streakCompleted_awards20Xp() {
    LocalDateTime completion = LocalDateTime.of(2026, 5, 17, 18, 0);
    var result =
        processor.process(
            profile,
            ActionEvent.STREAK_COMPLETED,
            completion,
            null,
            "streak-1",
            List.of(pastRecord(ActionEvent.TASK_COMPLETED, completion.minusDays(1))));

    assertEquals(20, result.getXpEarned());
  }

  private UserActivityRecord pastRecord(ActionEvent event, LocalDateTime date) {
    return UserActivityRecord.builder().actionEvent(event).completionDate(date).build();
  }
}
