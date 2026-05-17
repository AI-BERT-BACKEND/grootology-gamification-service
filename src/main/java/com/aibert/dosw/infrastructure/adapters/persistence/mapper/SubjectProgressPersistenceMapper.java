package com.aibert.dosw.infrastructure.adapters.persistence.mapper;

import com.aibert.dosw.domain.model.subject.ProgressVisualization;
import com.aibert.dosw.domain.model.subject.SubjectProgressSnapshot;
import com.aibert.dosw.infrastructure.adapters.persistence.entity.SubjectProgressEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SubjectProgressPersistenceMapper {

  @Mapping(target = "progressBarPercent", source = "progressVisualization.progressBarPercent")
  @Mapping(target = "xpDisplay", source = "progressVisualization.xpDisplay")
  @Mapping(target = "academicStatus", source = "progressVisualization.academicStatus")
  @Mapping(target = "statusColor", source = "progressVisualization.statusColor")
  @Mapping(target = "tasksCompletedLabel", source = "progressVisualization.tasksCompletedLabel")
  SubjectProgressEntity toEntity(SubjectProgressSnapshot snapshot);

  @Mapping(target = "progressVisualization", expression = "java(toVisualization(entity))")
  SubjectProgressSnapshot toDomain(SubjectProgressEntity entity);

  @Mapping(target = "progressBarPercent", source = "progressVisualization.progressBarPercent")
  @Mapping(target = "xpDisplay", source = "progressVisualization.xpDisplay")
  @Mapping(target = "academicStatus", source = "progressVisualization.academicStatus")
  @Mapping(target = "statusColor", source = "progressVisualization.statusColor")
  @Mapping(target = "tasksCompletedLabel", source = "progressVisualization.tasksCompletedLabel")
  void updateEntity(@MappingTarget SubjectProgressEntity entity, SubjectProgressSnapshot snapshot);

  default ProgressVisualization toVisualization(SubjectProgressEntity entity) {
    return ProgressVisualization.builder()
        .progressBarPercent(entity.getProgressBarPercent())
        .xpDisplay(entity.getXpDisplay())
        .academicStatus(entity.getAcademicStatus())
        .statusColor(entity.getStatusColor())
        .tasksCompletedLabel(entity.getTasksCompletedLabel())
        .build();
  }
}
