package com.aibert.dosw.application.mapper;

import com.aibert.dosw.application.dto.request.ActivityRecordDTO;
import com.aibert.dosw.domain.model.achievement.UserProgressRecord;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProgressDataMapper {

  UserProgressRecord toDomain(ActivityRecordDTO dto);

  List<UserProgressRecord> toDomainList(List<ActivityRecordDTO> records);
}
