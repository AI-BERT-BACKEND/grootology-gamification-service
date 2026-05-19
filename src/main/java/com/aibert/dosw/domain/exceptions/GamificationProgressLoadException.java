package com.aibert.dosw.domain.exceptions;

public class GamificationProgressLoadException extends GamificationException {
  public GamificationProgressLoadException(String message, Throwable cause) {
    super(ErrorCode.GAMIFICATION_PROGRESS_LOAD_FAILED, message, cause);
  }
}
