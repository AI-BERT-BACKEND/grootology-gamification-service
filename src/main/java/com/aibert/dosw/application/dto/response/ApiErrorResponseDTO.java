package com.aibert.dosw.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
@Schema(description = "Standard API error response")
public class ApiErrorResponseDTO {

  @Schema(description = "Business error code", example = "GAM-404")
  private final String code;

  @Schema(description = "Human-readable error message")
  private final String message;

  @Schema(description = "Error timestamp in UTC")
  private final Instant timestamp;

  @Schema(description = "Request path that triggered the error", example = "/api/v1/gamification/...")
  private final String path;
}
