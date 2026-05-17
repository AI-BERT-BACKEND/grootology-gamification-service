package com.aibert.dosw.infrastructure.adapters.persistence.mapper;

import com.aibert.dosw.domain.model.achievement.UnlockedAchievement;
import com.aibert.dosw.domain.model.user.GamificationProfile;
import com.aibert.dosw.infrastructure.adapters.persistence.entity.GamificationProfileEntity;
import com.aibert.dosw.infrastructure.adapters.persistence.entity.UnlockedAchievementEmbeddable;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GamificationPersistenceMapper {

  @Mapping(target = "achievements", source = "achievements")
  GamificationProfile toDomain(GamificationProfileEntity entity);

  @Mapping(target = "achievements", source = "achievements")
  GamificationProfileEntity toEntity(GamificationProfile profile);

  UnlockedAchievement toDomain(UnlockedAchievementEmbeddable embeddable);

  UnlockedAchievementEmbeddable toEmbeddable(UnlockedAchievement achievement);

  List<UnlockedAchievement> toDomainList(List<UnlockedAchievementEmbeddable> embeddables);

  List<UnlockedAchievementEmbeddable> toEmbeddableList(List<UnlockedAchievement> achievements);
}
