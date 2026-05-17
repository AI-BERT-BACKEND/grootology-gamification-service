package com.aibert.dosw.domain.exceptions;

public class SubjectProgressLoadException extends GamificationException {
  public SubjectProgressLoadException(String message, Throwable cause) {
    super(ErrorCode.SUBJECT_PROGRESS_LOAD_FAILED, message, cause);
  }
}
