package com.aibert.dosw.application.dto.request;

import com.aibert.dosw.domain.model.achievement.AchievementEvent;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class AchievementUnlockRequestDTO {

  @NotNull private AchievementEvent achievementEvent;

  @NotEmpty @Valid private List<ActivityRecordDTO> userProgressData;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime unlockDate;
}
