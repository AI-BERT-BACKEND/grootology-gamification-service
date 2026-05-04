package com.aibert.dosw.domain.ports.in;

import com.aibert.dosw.application.dto.request.ActionEventRequestDTO;
import com.aibert.dosw.application.dto.response.GamificationResponseDTO;

import java.util.UUID;

public interface GamificationUseCase {
    GamificationResponseDTO processEvent(UUID userId, ActionEventRequestDTO request);
    GamificationResponseDTO getProgress(UUID userId);
}
