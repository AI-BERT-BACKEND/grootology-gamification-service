package com.aibert.dosw.domain.ports.out;

import java.util.List;

public interface AcademicSummaryProviderPort {

  AcademicSummary fetchAcademicSummary(String studentId);

  record AcademicSummary(String studentId, Double academicGpa, List<AcademicSubject> subjects) {}

  record AcademicSubject(
      Long subjectId,
      String subjectName,
      String semester,
      Double overallAverage,
      List<EvaluationCut> cuts) {}

  record EvaluationCut(Long id, String cutName, Double cutPercentage, Double grade) {}
}
