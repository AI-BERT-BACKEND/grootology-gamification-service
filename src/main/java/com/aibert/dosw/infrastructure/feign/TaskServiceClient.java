package com.aibert.dosw.infrastructure.feign;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
    name = "task-service-client",
    url = "${clients.task.base-url:http://localhost:1503}",
    path = "/api/tasks",
    configuration = FeignClientConfig.class)
public interface TaskServiceClient {

  @GetMapping("/student/{studentId}")
  List<TaskSummaryResponse> getTasksByStudent(
      @RequestHeader("X-User-Id") String requesterUserId, @PathVariable String studentId);

  record TaskSummaryResponse(
      String id,
      String studentId,
      String subjectId,
      String title,
      String description,
      String taskType,
      Integer estimatedDurationMinutes,
      LocalDateTime deadline,
      String priority,
      String status,
      LocalDateTime scheduledDate,
      LocalDateTime completedAt,
      LocalDateTime changedAt) {}
}
