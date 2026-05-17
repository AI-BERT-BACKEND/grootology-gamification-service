package com.aibert.dosw.application.mapper;

import com.aibert.dosw.application.dto.request.UserActivityRecordDTO;
import com.aibert.dosw.domain.model.user.UserActivityRecord;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ActivityHistoryMapper {

  UserActivityRecord toDomain(UserActivityRecordDTO dto);

  List<UserActivityRecord> toDomainList(List<UserActivityRecordDTO> records);
}
