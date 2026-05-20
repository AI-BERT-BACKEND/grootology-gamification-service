package com.aibert.dosw.application.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AcademicSyncSubjectDTO {
  private final Long subjectId;
  private final String subjectName;
  private final String semester;
  private final Double overallAverage;
  private final List<AcademicSyncCutDTO> cuts;
}
