package com.aibert.dosw.entrypoints.advice;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.aibert.dosw.application.dto.response.ApiErrorResponseDTO;
import com.aibert.dosw.domain.exceptions.AchievementUpdateException;
import com.aibert.dosw.domain.exceptions.GamificationProfileNotFoundException;
import com.aibert.dosw.domain.exceptions.NoSubjectsRegisteredException;
import com.aibert.dosw.domain.exceptions.PointsUpdateException;
import com.aibert.dosw.domain.exceptions.SubjectProgressLoadException;
import com.aibert.dosw.domain.exceptions.SubjectProgressNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

class GlobalExceptionHandlerTest {

  private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

  @Test
  void handleGamification_returnsMappedStatus() {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getRequestURI()).thenReturn("/api/v1/gamification/1/points");

    ResponseEntity<ApiErrorResponseDTO> response =
        handler.handleGamification(new GamificationProfileNotFoundException(), request);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("GAM-404", response.getBody().getCode());
    assertNotNull(response.getBody().getTimestamp());
  }

  @Test
  void handleNoSubjects_returns404() {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getRequestURI()).thenReturn("/api/v1/gamification/1/subjects/progress");

    ResponseEntity<ApiErrorResponseDTO> response =
        handler.handleGamification(new NoSubjectsRegisteredException(), request);

    assertEquals("GAM-405", response.getBody().getCode());
  }

  @Test
  void handlePointsUpdate_returns500() {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getRequestURI()).thenReturn("/api/v1/gamification/1/points/events");
    ResponseEntity<ApiErrorResponseDTO> response =
        handler.handleGamification(
            new PointsUpdateException("Points failed", new RuntimeException()), request);
    assertEquals("GAM-500", response.getBody().getCode());
  }

  @Test
  void handleAchievementUpdate_returns501() {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getRequestURI()).thenReturn("/api/v1/gamification/1/achievements/unlock");
    ResponseEntity<ApiErrorResponseDTO> response =
        handler.handleGamification(
            new AchievementUpdateException("Achievements failed", new RuntimeException()), request);
    assertEquals("GAM-501", response.getBody().getCode());
  }

  @Test
  void handleSubjectProgressLoad_returns502() {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getRequestURI()).thenReturn("/api/v1/gamification/1/subjects/progress");
    ResponseEntity<ApiErrorResponseDTO> response =
        handler.handleGamification(
            new SubjectProgressLoadException("Load failed", new RuntimeException()), request);
    assertEquals("GAM-502", response.getBody().getCode());
  }

  @Test
  void handleGeneric_returns500() {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getRequestURI()).thenReturn("/api/v1/gamification/1/achievements");

    ResponseEntity<ApiErrorResponseDTO> response =
        handler.handleGeneric(new RuntimeException("boom"), request);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals("GAM-503", response.getBody().getCode());
  }

  @Test
  void handleUnreadable_returns400() {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getRequestURI()).thenReturn("/api/v1/gamification/1/points/events");

    ResponseEntity<ApiErrorResponseDTO> response =
        handler.handleUnreadable(new HttpMessageNotReadableException("invalid"), request);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("GAM-400", response.getBody().getCode());
  }

  @Test
  void handleTypeMismatch_returns400() {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getRequestURI()).thenReturn("/api/v1/gamification/not-uuid/points");

    ResponseEntity<ApiErrorResponseDTO> response =
        handler.handleTypeMismatch(mock(MethodArgumentTypeMismatchException.class), request);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("GAM-400", response.getBody().getCode());
  }
}
