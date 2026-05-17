package com.aibert.dosw.domain.exceptions;

import lombok.Getter;

@Getter
public class GamificationException extends RuntimeException {

  private final ErrorCode errorCode;

  public GamificationException(ErrorCode errorCode) {
    super(errorCode.getDefaultMessage());
    this.errorCode = errorCode;
  }

  public GamificationException(ErrorCode errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
  }

  public GamificationException(ErrorCode errorCode, String message, Throwable cause) {
    super(message, cause);
    this.errorCode = errorCode;
  }
}
