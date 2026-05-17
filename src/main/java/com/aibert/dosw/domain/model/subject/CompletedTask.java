package com.aibert.dosw.domain.model.subject;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CompletedTask {
  private String taskId;
  private String taskName;
  private LocalDateTime completedAt;
  private int xpValue;
}
