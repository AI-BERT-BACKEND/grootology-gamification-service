package com.aibert.dosw.application.mapper;

import com.aibert.dosw.application.dto.request.CompletedTaskDTO;
import com.aibert.dosw.application.dto.request.SubjectProgressDataDTO;
import com.aibert.dosw.application.dto.response.ProgressVisualizationDTO;
import com.aibert.dosw.application.dto.response.SubjectProgressItemDTO;
import com.aibert.dosw.domain.model.subject.CompletedTask;
import com.aibert.dosw.domain.model.subject.ProgressVisualization;
import com.aibert.dosw.domain.model.subject.SubjectProgressInput;
import com.aibert.dosw.domain.model.subject.SubjectProgressResult;
import com.aibert.dosw.domain.model.subject.SubjectProgressSnapshot;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SubjectProgressApplicationMapper {

  CompletedTask toDomain(CompletedTaskDTO dto);

  SubjectProgressInput toDomain(SubjectProgressDataDTO dto);

  ProgressVisualizationDTO toVisualizationDto(ProgressVisualization visualization);

  SubjectProgressItemDTO toItemDto(SubjectProgressResult result);

  SubjectProgressItemDTO toItemDto(SubjectProgressSnapshot snapshot);

  @Mapping(target = "progressVisualization", source = "progressVisualization")
  @Mapping(
      target = "message",
      expression =
          "java(snapshot.isPartialData() ? \"Partial progress: insufficient academic activity (FA-02).\" : null)")
  SubjectProgressItemDTO snapshotToItem(SubjectProgressSnapshot snapshot);
}
