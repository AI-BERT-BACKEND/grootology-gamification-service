package com.aibert.dosw.domain.model.user;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserActivityRecord {
    private ActionEvent actionEvent;
    private LocalDateTime completionDate;
    private LocalDateTime dueDate;
    private String activityId;
}
