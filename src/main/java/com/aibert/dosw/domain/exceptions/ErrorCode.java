package com.aibert.dosw.domain.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
  VALIDATION_ERROR("GAM-400", HttpStatus.BAD_REQUEST, "Invalid request payload"),
  PROFILE_NOT_FOUND("GAM-404", HttpStatus.NOT_FOUND, "Gamification profile not found"),
  NO_SUBJECTS_REGISTERED("GAM-405", HttpStatus.NOT_FOUND, "No registered subjects available for progress"),
  SUBJECT_NOT_FOUND("GAM-406", HttpStatus.NOT_FOUND, "Subject progress not found"),
  INVALID_SUBJECT_DATA("GAM-422", HttpStatus.UNPROCESSABLE_ENTITY, "Invalid academic data for subject"),
  POINTS_UPDATE_FAILED("GAM-500", HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update student points"),
  ACHIEVEMENT_UPDATE_FAILED("GAM-501", HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update student achievements"),
  SUBJECT_PROGRESS_LOAD_FAILED(
      "GAM-502", HttpStatus.INTERNAL_SERVER_ERROR, "Failed to load subject progress indicators"),
  INTERNAL_ERROR("GAM-503", HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected internal error");

  private final String code;
  private final HttpStatus httpStatus;
  private final String defaultMessage;

  ErrorCode(String code, HttpStatus httpStatus, String defaultMessage) {
    this.code = code;
    this.httpStatus = httpStatus;
    this.defaultMessage = defaultMessage;
  }
}
