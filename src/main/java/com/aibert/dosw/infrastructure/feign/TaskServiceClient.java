package com.aibert.dosw.infrastructure.feign;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "task-service-client",
    url = "${clients.task.base-url:http://localhost:8082}",
    path = "/api/v1/tasks",
    configuration = FeignClientConfig.class)
public interface TaskServiceClient {

  @GetMapping("/users/{userId}")
  TaskApiResponse<TaskListResponse> getTasksByUser(
      @PathVariable UUID userId, @RequestParam(required = false) String status);

  @GetMapping("/users/{userId}/completed")
  TaskApiResponse<TaskListResponse> getCompletedTasksByUser(@PathVariable UUID userId);

  record TaskApiResponse<T>(boolean success, T data, String message, String error, Integer code) {}

  record TaskListResponse(List<TaskSummaryResponse> tasks) {}

  record TaskSummaryResponse(
      UUID taskId,
      String title,
      String status,
      Integer xpValue,
      LocalDateTime completedAt,
      LocalDateTime dueDate) {}
}
