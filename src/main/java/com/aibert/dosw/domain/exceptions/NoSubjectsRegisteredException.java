package com.aibert.dosw.domain.exceptions;

public class NoSubjectsRegisteredException extends GamificationException {
  public NoSubjectsRegisteredException() {
    super(ErrorCode.NO_SUBJECTS_REGISTERED);
  }
}
