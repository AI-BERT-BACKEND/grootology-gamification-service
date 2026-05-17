package com.aibert.dosw.domain.model.achievement;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** Condiciones internas de desbloqueo definidas por el sistema (AIB-37). */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AchievementConditions {

  public static final int TASK_STREAK_REQUIRED_DAYS = 7;
  public static final int PRODUCTIVITY_STREAK_REQUIRED_DAYS = 14;
  public static final int PERFECT_SCORE_REQUIRED = 100;
  public static final int SUBJECT_MASTERY_PROGRESS_PERCENT = 100;
  public static final int RECENT_ACHIEVEMENTS_LIMIT = 5;
}
