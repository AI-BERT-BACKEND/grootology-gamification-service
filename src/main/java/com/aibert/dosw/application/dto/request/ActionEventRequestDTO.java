package com.aibert.dosw.application.dto.request;

import com.aibert.dosw.domain.model.user.ActionEvent;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class ActionEventRequestDTO {

  @NotNull private ActionEvent actionEvent;

  @NotEmpty @Valid private List<UserActivityRecordDTO> userActivityHistory;

  @NotNull
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime completionDate;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime dueDate;

  private UUID activityId;
}
