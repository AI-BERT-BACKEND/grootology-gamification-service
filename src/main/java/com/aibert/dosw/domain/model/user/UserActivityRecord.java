package com.aibert.dosw.domain.model.user;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class UserActivityRecord {
    private ActionEvent actionEvent;
    private LocalDateTime completionDate;
    private LocalDateTime dueDate;
    private UUID activityId;
}
