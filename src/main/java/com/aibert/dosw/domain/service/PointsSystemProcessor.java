package com.aibert.dosw.domain.service;

import com.aibert.dosw.domain.model.user.ActionEvent;
import com.aibert.dosw.domain.model.user.GamificationProfile;
import com.aibert.dosw.domain.model.user.UserActivityRecord;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

/**
 * Lógica de negocio del Sistema de Puntos (AIB-36).
 */
public class PointsSystemProcessor {

    private static final int XP_TASK_COMPLETED = 10;
    private static final int XP_PUNCTUALITY_BONUS = 5;
    private static final int XP_STREAK_COMPLETED = 20;
    private static final int XP_WEEKLY_GOAL = 25;
    private static final int XP_SUBJECT_PROGRESS = 8;

    public PointsAwardResult process(
            GamificationProfile profile,
            ActionEvent actionEvent,
            LocalDateTime completionDate,
            LocalDateTime dueDate,
            UUID activityId,
            List<UserActivityRecord> userActivityHistory) {

        if (!isValidRequest(actionEvent, completionDate, userActivityHistory)) {
            return unchanged(profile, "Action does not meet conditions to award points (FA-01).");
        }

        if (isDuplicateEvent(actionEvent, completionDate, activityId, userActivityHistory)) {
            return unchanged(profile, "Duplicate event: no additional points awarded (FA-04).");
        }

        int streak = resolveStreak(profile, completionDate);

        int xpEarned = calculateXp(actionEvent, completionDate, dueDate);
        if (actionEvent == ActionEvent.TASK_COMPLETED && !isOnTime(completionDate, dueDate)) {
            xpEarned = XP_TASK_COMPLETED;
        }

        int totalPoints = profile.getTotalPoints() + xpEarned;
        if (countsTowardStreak(actionEvent)) {
            streak = incrementStreak(profile, completionDate, streak);
        }

        return PointsAwardResult.builder()
                .xpEarned(xpEarned)
                .totalPoints(totalPoints)
                .currentStreak(streak)
                .lastActivityDate(completionDate.toLocalDate())
                .pointsUpdated(true)
                .message(null)
                .build();
    }

    private boolean isValidRequest(
            ActionEvent actionEvent,
            LocalDateTime completionDate,
            List<UserActivityRecord> history) {
        return actionEvent != null
                && completionDate != null
                && history != null
                && !history.isEmpty();
    }

    private boolean isDuplicateEvent(
            ActionEvent actionEvent,
            LocalDateTime completionDate,
            UUID activityId,
            List<UserActivityRecord> history) {
        if (activityId != null) {
            return history.stream().anyMatch(record -> activityId.equals(record.getActivityId()));
        }
        return history.stream().anyMatch(record ->
                record.getActivityId() == null
                        && record.getActionEvent() == actionEvent
                        && record.getCompletionDate() != null
                        && record.getCompletionDate().equals(completionDate));
    }

    private int resolveStreak(GamificationProfile profile, LocalDateTime completionDate) {
        LocalDate today = completionDate.toLocalDate();
        LocalDate lastActivity = profile.getLastActivityDate();

        if (lastActivity == null) {
            return profile.getCurrentStreak();
        }

        long daysSinceLast = ChronoUnit.DAYS.between(lastActivity, today);
        if (daysSinceLast > 1) {
            return 0;
        }
        return profile.getCurrentStreak();
    }

    private int incrementStreak(GamificationProfile profile, LocalDateTime completionDate, int currentStreak) {
        LocalDate today = completionDate.toLocalDate();
        LocalDate lastActivity = profile.getLastActivityDate();

        if (lastActivity == null) {
            return 1;
        }
        if (lastActivity.equals(today)) {
            return Math.max(currentStreak, profile.getCurrentStreak());
        }
        if (lastActivity.plusDays(1).equals(today)) {
            return profile.getCurrentStreak() + 1;
        }
        return 1;
    }

    private boolean countsTowardStreak(ActionEvent actionEvent) {
        return actionEvent == ActionEvent.TASK_COMPLETED
                || actionEvent == ActionEvent.STREAK_COMPLETED
                || actionEvent == ActionEvent.WEEKLY_GOAL_COMPLETED
                || actionEvent == ActionEvent.SUBJECT_PROGRESS;
    }

    private int calculateXp(ActionEvent actionEvent, LocalDateTime completionDate, LocalDateTime dueDate) {
        return switch (actionEvent) {
            case TASK_COMPLETED -> isOnTime(completionDate, dueDate)
                    ? XP_TASK_COMPLETED + XP_PUNCTUALITY_BONUS
                    : XP_TASK_COMPLETED;
            case STREAK_COMPLETED -> XP_STREAK_COMPLETED;
            case WEEKLY_GOAL_COMPLETED -> XP_WEEKLY_GOAL;
            case SUBJECT_PROGRESS -> XP_SUBJECT_PROGRESS;
        };
    }

    private boolean isOnTime(LocalDateTime completionDate, LocalDateTime dueDate) {
        if (dueDate == null) {
            return true;
        }
        return !completionDate.isAfter(dueDate);
    }

    private PointsAwardResult unchanged(GamificationProfile profile, String message) {
        return PointsAwardResult.builder()
                .xpEarned(0)
                .totalPoints(profile.getTotalPoints())
                .currentStreak(profile.getCurrentStreak())
                .lastActivityDate(profile.getLastActivityDate())
                .pointsUpdated(false)
                .message(message)
                .build();
    }

    @lombok.Getter
    @lombok.Builder
    public static class PointsAwardResult {
        private final int xpEarned;
        private final int totalPoints;
        private final int currentStreak;
        private final LocalDate lastActivityDate;
        private final boolean pointsUpdated;
        private final String message;
    }
}
