package com.aibert.dosw.application.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AcademicSyncCutDTO {
  private final Long id;
  private final String cutName;
  private final Double cutPercentage;
  private final Double grade;
}
