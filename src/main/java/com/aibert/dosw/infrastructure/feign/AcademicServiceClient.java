package com.aibert.dosw.infrastructure.feign;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
    name = "academic-service-client",
    url = "${clients.academic.base-url:http://localhost:1502}",
    path = "/api/v1/academic",
    configuration = FeignClientConfig.class)
public interface AcademicServiceClient {

  @GetMapping("/summary")
  AcademicApiResponse<AcademicSummaryResponse> getAcademicSummary(
      @RequestHeader("X-Student-Id") String studentId);

  record AcademicApiResponse<T>(
      boolean success, T data, String message, String error, Integer code) {}

  record AcademicSummaryResponse(
      String studentId, Double academicGpa, List<AcademicSubjectResponse> subjects) {}

  record AcademicSubjectResponse(
      Long subjectId,
      String subjectName,
      String semester,
      Double overallAverage,
      List<EvaluationCutResponse> cuts) {}

  record EvaluationCutResponse(Long id, String cutName, Double cutPercentage, Double grade) {}
}
