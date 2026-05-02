package com.aibert.dosw.infrastructure.adapters.persistence.mapper;

import com.aibert.dosw.domain.model.user.GamificationProfile;
import com.aibert.dosw.infrastructure.adapters.persistence.entity.GamificationProfileEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GamificationPersistenceMapper {
    GamificationProfile toDomain(GamificationProfileEntity entity);
    GamificationProfileEntity toEntity(GamificationProfile profile);
}
