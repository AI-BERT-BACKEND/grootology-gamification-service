package com.aibert.dosw.domain.exceptions;

public class PointsUpdateException extends GamificationException {
  public PointsUpdateException(String message, Throwable cause) {
    super(ErrorCode.POINTS_UPDATE_FAILED, message, cause);
  }
}
