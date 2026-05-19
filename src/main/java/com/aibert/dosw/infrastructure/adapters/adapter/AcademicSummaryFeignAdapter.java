package com.aibert.dosw.infrastructure.adapters.adapter;

import com.aibert.dosw.domain.ports.out.AcademicSummaryProviderPort;
import com.aibert.dosw.infrastructure.feign.AcademicServiceClient;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AcademicSummaryFeignAdapter implements AcademicSummaryProviderPort {

  private final AcademicServiceClient academicServiceClient;

  @Override
  public AcademicSummary fetchAcademicSummary(String studentId) {
    var response = academicServiceClient.getAcademicSummary(studentId);
    if (response == null) {
      throw new IllegalStateException("academic-service returned an empty response.");
    }
    if (!response.success()) {
      String error = response.error() != null ? response.error() : "unknown error";
      throw new IllegalStateException(
          "academic-service returned an error: " + error + " (code=" + response.code() + ")");
    }
    if (response.data() == null) {
      throw new IllegalStateException("academic-service returned success without data.");
    }

    return new AcademicSummary(
        response.data().studentId(),
        response.data().academicGpa(),
        mapSubjects(response.data().subjects()));
  }

  private List<AcademicSubject> mapSubjects(List<AcademicServiceClient.AcademicSubjectResponse> subjects) {
    if (subjects == null || subjects.isEmpty()) {
      return List.of();
    }

    return subjects.stream().map(this::mapSubject).toList();
  }

  private AcademicSubject mapSubject(AcademicServiceClient.AcademicSubjectResponse subject) {
    return new AcademicSubject(
        subject.subjectId(),
        subject.subjectName(),
        subject.semester(),
        subject.overallAverage(),
        mapCuts(subject.cuts()));
  }

  private List<EvaluationCut> mapCuts(List<AcademicServiceClient.EvaluationCutResponse> cuts) {
    if (cuts == null || cuts.isEmpty()) {
      return List.of();
    }

    return cuts.stream()
        .map(cut -> new EvaluationCut(cut.id(), cut.cutName(), cut.cutPercentage(), cut.grade()))
        .toList();
  }
}
