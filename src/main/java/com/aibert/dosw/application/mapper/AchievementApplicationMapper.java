package com.aibert.dosw.application.mapper;

import com.aibert.dosw.application.dto.response.AchievementBadgeDTO;
import com.aibert.dosw.application.dto.response.AchievementGalleryItemDTO;
import com.aibert.dosw.application.dto.response.AchievementResponseDTO;
import com.aibert.dosw.domain.model.achievement.AchievementCatalog;
import com.aibert.dosw.domain.model.achievement.AchievementDefinition;
import com.aibert.dosw.domain.model.achievement.UnlockedAchievement;
import com.aibert.dosw.domain.service.AchievementSystemProcessor;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AchievementApplicationMapper {

  AchievementBadgeDTO toBadgeDto(AchievementDefinition definition);

  @Mapping(target = "event", source = "definition.event")
  @Mapping(target = "name", source = "definition.name")
  @Mapping(target = "icon", source = "definition.icon")
  @Mapping(target = "description", source = "definition.description")
  @Mapping(target = "unlocked", source = "unlocked")
  @Mapping(target = "pending", expression = "java(!entry.isUnlocked())")
  @Mapping(target = "unlockDate", source = "unlockDate")
  AchievementGalleryItemDTO toGalleryItem(AchievementSystemProcessor.AchievementGalleryEntry entry);

  default List<AchievementGalleryItemDTO> toGalleryDto(
      List<AchievementSystemProcessor.AchievementGalleryEntry> entries) {
    return entries.stream().map(this::toGalleryItem).toList();
  }

  default List<AchievementGalleryItemDTO> toRecentDto(List<UnlockedAchievement> recent) {
    return recent.stream()
        .map(
            unlocked ->
                AchievementCatalog.find(unlocked.getEvent())
                    .map(
                        def ->
                            AchievementGalleryItemDTO.builder()
                                .event(def.getEvent())
                                .name(def.getName())
                                .icon(def.getIcon())
                                .description(def.getDescription())
                                .unlockDate(unlocked.getUnlockDate())
                                .unlocked(true)
                                .pending(false)
                                .build())
                    .orElse(
                        AchievementGalleryItemDTO.builder()
                            .event(unlocked.getEvent())
                            .unlockDate(unlocked.getUnlockDate())
                            .unlocked(true)
                            .pending(false)
                            .build()))
        .toList();
  }

  default AchievementResponseDTO toUnlockResponse(
      String username,
      boolean unlocked,
      AchievementDefinition definition,
      List<AchievementGalleryItemDTO> gallery,
      List<AchievementGalleryItemDTO> recent,
      String message) {
    return AchievementResponseDTO.builder()
        .username(username)
        .achievementUnlocked(unlocked)
        .achievementBadge(toBadgeDto(definition))
        .achievementGallery(gallery)
        .recentAchievements(recent)
        .message(message)
        .build();
  }
}
