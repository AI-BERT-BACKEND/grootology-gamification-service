package com.aibert.dosw.domain.ports.in;

import com.aibert.dosw.application.dto.response.GamificationProgressResponseDTO;
import java.util.UUID;

public interface GamificationProgressUseCase {
  GamificationProgressResponseDTO getGamificationProgress(UUID userId);
}
