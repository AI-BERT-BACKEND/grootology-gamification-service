package com.aibert.dosw.application.dto.response;

import com.aibert.dosw.domain.model.achievement.AchievementEvent;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AchievementGalleryItemDTO {
  private AchievementEvent event;
  private String name;
  private String icon;
  private String description;
  private boolean unlocked;
  private boolean pending;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime unlockDate;
}
