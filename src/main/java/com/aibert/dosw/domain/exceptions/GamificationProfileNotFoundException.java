package com.aibert.dosw.domain.exceptions;

public class GamificationProfileNotFoundException extends GamificationException {
  public GamificationProfileNotFoundException() {
    super(ErrorCode.PROFILE_NOT_FOUND);
  }
}
