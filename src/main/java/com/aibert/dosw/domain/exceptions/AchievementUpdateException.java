package com.aibert.dosw.domain.exceptions;

public class AchievementUpdateException extends GamificationException {
  public AchievementUpdateException(String message, Throwable cause) {
    super(ErrorCode.ACHIEVEMENT_UPDATE_FAILED, message, cause);
  }
}
