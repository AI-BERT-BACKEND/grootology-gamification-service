package com.aibert.dosw.domain.exceptions;

public class SubjectProgressNotFoundException extends GamificationException {
  public SubjectProgressNotFoundException() {
    super(ErrorCode.SUBJECT_NOT_FOUND);
  }
}
