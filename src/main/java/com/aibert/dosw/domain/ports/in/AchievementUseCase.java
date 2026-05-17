package com.aibert.dosw.domain.ports.in;

import com.aibert.dosw.application.dto.request.AchievementUnlockRequestDTO;
import com.aibert.dosw.application.dto.response.AchievementResponseDTO;

import java.util.UUID;

public interface AchievementUseCase {
  AchievementResponseDTO unlockAchievement(UUID userId, AchievementUnlockRequestDTO request);

  AchievementResponseDTO getGallery(UUID userId);
}
