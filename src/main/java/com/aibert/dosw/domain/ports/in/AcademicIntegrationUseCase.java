package com.aibert.dosw.domain.ports.in;

import com.aibert.dosw.application.dto.response.AcademicSyncCheckResponseDTO;
import java.util.UUID;

public interface AcademicIntegrationUseCase {
  AcademicSyncCheckResponseDTO checkAcademicFeignConnection(UUID userId, String studentId);
}
