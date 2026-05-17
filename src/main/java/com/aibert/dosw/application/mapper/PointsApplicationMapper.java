package com.aibert.dosw.application.mapper;

import com.aibert.dosw.application.dto.response.PointsResponseDTO;
import com.aibert.dosw.domain.model.user.GamificationProfile;
import com.aibert.dosw.domain.service.PointsSystemProcessor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PointsApplicationMapper {

  @Mapping(target = "totalPoints", source = "award.totalPoints")
  @Mapping(target = "xpEarned", source = "award.xpEarned")
  @Mapping(target = "currentStreak", source = "award.currentStreak")
  @Mapping(target = "pointsUpdated", source = "award.pointsUpdated")
  @Mapping(target = "message", source = "award.message")
  PointsResponseDTO toResponse(PointsSystemProcessor.PointsAwardResult award);

  @Mapping(target = "xpEarned", expression = "java(0)")
  @Mapping(target = "pointsUpdated", expression = "java(false)")
  @Mapping(target = "message", ignore = true)
  PointsResponseDTO toQueryResponse(GamificationProfile profile);
}
