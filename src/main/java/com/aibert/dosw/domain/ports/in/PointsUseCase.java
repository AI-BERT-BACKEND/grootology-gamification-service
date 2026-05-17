package com.aibert.dosw.domain.ports.in;

import com.aibert.dosw.application.dto.request.ActionEventRequestDTO;
import com.aibert.dosw.application.dto.response.PointsResponseDTO;
import java.util.UUID;

public interface PointsUseCase {
  PointsResponseDTO processAcademicEvent(UUID userId, ActionEventRequestDTO request);

  PointsResponseDTO getPointsSummary(UUID userId);
}
