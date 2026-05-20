package com.aibert.dosw.application.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AcademicSyncCheckResponseDTO {
  private final String userId;
  private final String studentId;
  private final Double academicGpa;
  private final int totalSubjects;
  private final List<AcademicSyncSubjectDTO> subjects;
}
