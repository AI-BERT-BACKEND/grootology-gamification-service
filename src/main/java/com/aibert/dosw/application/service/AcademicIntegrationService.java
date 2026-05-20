package com.aibert.dosw.application.service;

import com.aibert.dosw.application.dto.response.AcademicSyncCheckResponseDTO;
import com.aibert.dosw.application.dto.response.AcademicSyncCutDTO;
import com.aibert.dosw.application.dto.response.AcademicSyncSubjectDTO;
import com.aibert.dosw.domain.ports.in.AcademicIntegrationUseCase;
import com.aibert.dosw.domain.ports.out.AcademicSummaryProviderPort;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AcademicIntegrationService implements AcademicIntegrationUseCase {

  private final AcademicSummaryProviderPort academicSummaryProvider;

  @Override
  public AcademicSyncCheckResponseDTO checkAcademicFeignConnection(UUID userId, String studentId) {
    String resolvedStudentId =
        (studentId == null || studentId.isBlank()) ? userId.toString() : studentId;
    var summary = academicSummaryProvider.fetchAcademicSummary(resolvedStudentId);

    List<AcademicSyncSubjectDTO> subjects =
        summary.subjects() == null
            ? List.of()
            : summary.subjects().stream()
                .map(
                    subject ->
                        AcademicSyncSubjectDTO.builder()
                            .subjectId(subject.subjectId())
                            .subjectName(subject.subjectName())
                            .semester(subject.semester())
                            .overallAverage(subject.overallAverage())
                            .cuts(
                                subject.cuts() == null
                                    ? List.of()
                                    : subject.cuts().stream()
                                        .map(
                                            cut ->
                                                AcademicSyncCutDTO.builder()
                                                    .id(cut.id())
                                                    .cutName(cut.cutName())
                                                    .cutPercentage(cut.cutPercentage())
                                                    .grade(cut.grade())
                                                    .build())
                                        .toList())
                            .build())
                .toList();

    return AcademicSyncCheckResponseDTO.builder()
        .userId(userId.toString())
        .studentId(summary.studentId())
        .academicGpa(summary.academicGpa())
        .totalSubjects(subjects.size())
        .subjects(subjects)
        .build();
  }
}
