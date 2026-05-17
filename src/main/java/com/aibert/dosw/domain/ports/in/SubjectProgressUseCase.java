package com.aibert.dosw.domain.ports.in;

import com.aibert.dosw.application.dto.request.SubjectProgressBatchRequestDTO;
import com.aibert.dosw.application.dto.response.SubjectProgressItemDTO;
import com.aibert.dosw.application.dto.response.SubjectProgressOverviewDTO;

import java.util.UUID;

public interface SubjectProgressUseCase {
  SubjectProgressOverviewDTO updateProgress(UUID userId, SubjectProgressBatchRequestDTO request);

  SubjectProgressOverviewDTO getProgressOverview(UUID userId);

  SubjectProgressItemDTO getSubjectProgress(UUID userId, String subjectId);
}
