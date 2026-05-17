package com.aibert.dosw.entrypoints.advice;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.aibert.dosw.application.dto.response.ApiErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

class GlobalExceptionHandlerValidationTest {

  private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

  @Test
  void handleValidation_returnsBadRequest() {
    MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
    BindingResult bindingResult = mock(BindingResult.class);
    HttpServletRequest request = mock(HttpServletRequest.class);

    when(ex.getBindingResult()).thenReturn(bindingResult);
    when(bindingResult.getFieldErrors()).thenReturn(java.util.List.of(new FieldError("dto", "field", "must not be null")));
    when(request.getRequestURI()).thenReturn("/api/v1/gamification/1/points/events");

    ResponseEntity<ApiErrorResponseDTO> response = handler.handleValidation(ex, request);

    assertEquals("GAM-400", response.getBody().getCode());
    assertTrue(response.getBody().getMessage().contains("field"));
  }
}
