package com.aibert.dosw.entrypoints.advice;

import com.aibert.dosw.application.dto.response.ApiErrorResponseDTO;
import com.aibert.dosw.domain.exceptions.ErrorCode;
import com.aibert.dosw.domain.exceptions.GamificationException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiErrorResponseDTO> handleValidation(
      MethodArgumentNotValidException ex, HttpServletRequest request) {
    String message =
        ex.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .findFirst()
            .orElse(ErrorCode.VALIDATION_ERROR.getDefaultMessage());
    return buildResponse(ErrorCode.VALIDATION_ERROR, message, request.getRequestURI());
  }

  @ExceptionHandler(GamificationException.class)
  public ResponseEntity<ApiErrorResponseDTO> handleGamification(
      GamificationException ex, HttpServletRequest request) {
    return buildResponse(ex.getErrorCode(), ex.getMessage(), request.getRequestURI());
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiErrorResponseDTO> handleUnreadable(
      HttpMessageNotReadableException ex, HttpServletRequest request) {
    return buildResponse(
        ErrorCode.VALIDATION_ERROR,
        ErrorCode.VALIDATION_ERROR.getDefaultMessage(),
        request.getRequestURI());
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ApiErrorResponseDTO> handleTypeMismatch(
      MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
    return buildResponse(
        ErrorCode.VALIDATION_ERROR,
        ErrorCode.VALIDATION_ERROR.getDefaultMessage(),
        request.getRequestURI());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiErrorResponseDTO> handleGeneric(
      Exception ex, HttpServletRequest request) {
    return buildResponse(
        ErrorCode.INTERNAL_ERROR,
        ErrorCode.INTERNAL_ERROR.getDefaultMessage(),
        request.getRequestURI());
  }

  private ResponseEntity<ApiErrorResponseDTO> buildResponse(
      ErrorCode errorCode, String message, String path) {
    ApiErrorResponseDTO body =
        ApiErrorResponseDTO.builder()
            .code(errorCode.getCode())
            .message(message)
            .timestamp(Instant.now())
            .path(path)
            .build();
    return ResponseEntity.status(errorCode.getHttpStatus()).body(body);
  }
}
