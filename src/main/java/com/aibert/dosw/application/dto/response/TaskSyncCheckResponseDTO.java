package com.aibert.dosw.application.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TaskSyncCheckResponseDTO {
  private final String userId;
  private final int totalTasks;
  private final List<TaskSyncItemDTO> tasks;
}
