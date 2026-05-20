package com.aibert.dosw.entrypoints.rest.controller;

import com.aibert.dosw.application.dto.request.ActionEventRequestDTO;
import com.aibert.dosw.application.dto.request.TaskCompletedEventRequestDTO;
import com.aibert.dosw.application.dto.request.UserActivityRecordDTO;
import com.aibert.dosw.application.dto.response.PointsResponseDTO;
import com.aibert.dosw.domain.model.user.ActionEvent;
import com.aibert.dosw.domain.ports.in.PointsUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
@Tag(
    name = "Task Events",
    description = "Compatibility endpoint for task-service completed-task notifications")
@SecurityRequirement(name = "Bearer")
public class TaskEventsController {

  private final PointsUseCase pointsUseCase;

  @Operation(
      summary = "Consume task completed event",
      description =
          "Backward-compatible endpoint used by task-service to notify completed tasks via Feign.")
  @PostMapping(value = "/task-completed", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<PointsResponseDTO> taskCompleted(
      @Valid @RequestBody TaskCompletedEventRequestDTO request) {
    UUID activityId = parseUuidOrNull(request.getTaskId());
    LocalDateTime completionDate =
        request.getCompletionDate() != null ? request.getCompletionDate() : LocalDateTime.now();

    UserActivityRecordDTO previousRecord = new UserActivityRecordDTO();
    previousRecord.setActionEvent(ActionEvent.SUBJECT_PROGRESS);
    previousRecord.setCompletionDate(completionDate.minusSeconds(1));

    ActionEventRequestDTO pointsRequest = new ActionEventRequestDTO();
    pointsRequest.setActionEvent(ActionEvent.TASK_COMPLETED);
    pointsRequest.setCompletionDate(completionDate);
    pointsRequest.setDueDate(request.getDueDate());
    pointsRequest.setActivityId(activityId);
    pointsRequest.setUserActivityHistory(List.of(previousRecord));

    return ResponseEntity.ok(pointsUseCase.processAcademicEvent(request.getStudentId(), pointsRequest));
  }

  private UUID parseUuidOrNull(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }
    try {
      return UUID.fromString(value);
    } catch (IllegalArgumentException ex) {
      return null;
    }
  }
}
