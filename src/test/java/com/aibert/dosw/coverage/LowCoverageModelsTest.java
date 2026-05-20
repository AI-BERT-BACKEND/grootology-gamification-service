package com.aibert.dosw.coverage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aibert.dosw.application.dto.response.AchievementBadgeDTO;
import com.aibert.dosw.application.dto.response.AchievementGalleryItemDTO;
import com.aibert.dosw.domain.model.achievement.AchievementEvent;
import com.aibert.dosw.domain.model.achievement.UserProgressRecord;
import com.aibert.dosw.domain.model.subject.CompletedTask;
import com.aibert.dosw.domain.model.user.ActionEvent;
import com.aibert.dosw.domain.model.user.UserActivityRecord;
import com.aibert.dosw.infrastructure.adapters.persistence.entity.UnlockedAchievementEmbeddable;
import com.aibert.dosw.infrastructure.feign.IdentityServiceClient;
import com.aibert.dosw.infrastructure.feign.TaskServiceClient;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class LowCoverageModelsTest {

  @Test
  void achievementDtos_shouldExposeValues() {
    LocalDateTime now = LocalDateTime.of(2026, 5, 19, 10, 30);

    AchievementGalleryItemDTO galleryItem =
        AchievementGalleryItemDTO.builder()
            .event(AchievementEvent.TASK_STREAK)
            .name("Constante")
            .icon("badge-streak")
            .description("7 days")
            .unlocked(true)
            .pending(false)
            .unlockDate(now)
            .build();

    assertEquals(AchievementEvent.TASK_STREAK, galleryItem.getEvent());
    assertEquals("Constante", galleryItem.getName());
    assertTrue(galleryItem.isUnlocked());
    assertFalse(galleryItem.isPending());
    assertEquals(now, galleryItem.getUnlockDate());

    AchievementBadgeDTO badge =
        AchievementBadgeDTO.builder()
            .event(AchievementEvent.PERFECT_SCORE)
            .name("Perfecto")
            .icon("badge-perfect")
            .description("100 points")
            .build();

    assertEquals(AchievementEvent.PERFECT_SCORE, badge.getEvent());
    assertEquals("Perfecto", badge.getName());
    assertEquals("badge-perfect", badge.getIcon());
    assertEquals("100 points", badge.getDescription());
  }

  @Test
  void domainModels_shouldExposeValues() {
    LocalDateTime now = LocalDateTime.of(2026, 5, 19, 11, 0);
    UUID subjectId = UUID.randomUUID();
    UUID activityId = UUID.randomUUID();

    CompletedTask task =
        CompletedTask.builder()
            .taskId("t-1")
            .taskName("Quiz")
            .completedAt(now)
            .xpValue(10)
            .build();

    assertEquals("t-1", task.getTaskId());
    assertEquals("Quiz", task.getTaskName());
    assertEquals(now, task.getCompletedAt());
    assertEquals(10, task.getXpValue());

    UserProgressRecord progress =
        UserProgressRecord.builder()
            .activityType("GOAL_COMPLETED")
            .completionDate(now)
            .score(100)
            .progressPercent(90)
            .streakDays(7)
            .subjectId(subjectId)
            .build();

    assertEquals("GOAL_COMPLETED", progress.getActivityType());
    assertEquals(100, progress.getScore());
    assertEquals(90, progress.getProgressPercent());
    assertEquals(7, progress.getStreakDays());
    assertEquals(subjectId, progress.getSubjectId());

    UserActivityRecord activity =
        UserActivityRecord.builder()
            .actionEvent(ActionEvent.TASK_COMPLETED)
            .completionDate(now)
            .dueDate(now.plusHours(2))
            .activityId(activityId)
            .build();

    assertEquals(ActionEvent.TASK_COMPLETED, activity.getActionEvent());
    assertEquals(activityId, activity.getActivityId());
  }

  @Test
  void embeddableAndFeignRecords_shouldExposeValues() {
    LocalDateTime now = LocalDateTime.of(2026, 5, 19, 12, 0);
    UUID id = UUID.randomUUID();

    UnlockedAchievementEmbeddable embeddable = new UnlockedAchievementEmbeddable();
    embeddable.setEvent(AchievementEvent.PRODUCTIVITY_STREAK);
    embeddable.setUnlockDate(now);

    assertEquals(AchievementEvent.PRODUCTIVITY_STREAK, embeddable.getEvent());
    assertEquals(now, embeddable.getUnlockDate());

    IdentityServiceClient.IdentityUserResponse identity =
        new IdentityServiceClient.IdentityUserResponse(id, "student@mail.com", "student");

    assertEquals(id, identity.id());
    assertEquals("student@mail.com", identity.email());
    assertEquals("student", identity.username());

    TaskServiceClient.TaskSummaryResponse summary =
        new TaskServiceClient.TaskSummaryResponse(
            "task-1",
            id.toString(),
            "subject-1",
            "Task 1",
            "Description",
            "HOMEWORK",
            45,
            now.plusHours(1),
            "HIGH",
            "COMPLETED",
            now.minusHours(2),
            now.minusHours(1),
            now);

    assertEquals("task-1", summary.id());
    assertEquals(id.toString(), summary.studentId());
    assertEquals("COMPLETED", summary.status());
    assertNotNull(summary.completedAt());
    assertNotNull(summary.changedAt());
  }
}
