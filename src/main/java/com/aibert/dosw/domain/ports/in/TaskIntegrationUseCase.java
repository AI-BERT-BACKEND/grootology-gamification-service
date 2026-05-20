package com.aibert.dosw.domain.ports.in;

import com.aibert.dosw.application.dto.response.TaskSyncCheckResponseDTO;
import java.util.UUID;

public interface TaskIntegrationUseCase {
  TaskSyncCheckResponseDTO checkTaskFeignConnection(UUID userId);
}
