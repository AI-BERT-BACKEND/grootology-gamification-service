package com.aibert.dosw.domain.service;

import static org.junit.jupiter.api.Assertions.*;

import com.aibert.dosw.domain.model.subject.CompletedTask;
import com.aibert.dosw.domain.model.subject.SubjectProgressInput;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class SubjectProgressProcessorTest {

  private final SubjectProgressProcessor processor = new SubjectProgressProcessor();

  @Test
  void calculate_validSubject_clampsPercentage() {
    var result =
        processor.calculate(
            SubjectProgressInput.builder()
                .subjectId("math-101")
                .subjectName("Mathematics")
                .completedTasks(
                    List.of(
                        CompletedTask.builder().taskId("t1").xpValue(10).build(),
                        CompletedTask.builder().taskId("t2").xpValue(10).build()))
                .totalTasks(4)
                .academicPerformance(85f)
                .build());

    assertTrue(result.isValid());
    assertTrue(result.getSubjectProgressPercentage() >= 0);
    assertTrue(result.getSubjectProgressPercentage() <= 100);
  }

  @Test
  void calculate_invalidSubject_fa04() {
    var result =
        processor.calculate(
            SubjectProgressInput.builder()
                .subjectId("")
                .academicPerformance(80f)
                .completedTasks(List.of())
                .build());

    assertFalse(result.isValid());
  }

  @Test
  void calculate_highProgress_completedStatus() {
    var result =
        processor.calculate(
            SubjectProgressInput.builder()
                .subjectId("chem-01")
                .completedTasks(
                    List.of(
                        CompletedTask.builder().taskId("t1").build(),
                        CompletedTask.builder().taskId("t2").build(),
                        CompletedTask.builder().taskId("t3").build(),
                        CompletedTask.builder().taskId("t4").build()))
                .totalTasks(4)
                .academicPerformance(100f)
                .build());

    assertEquals("COMPLETED", result.getProgressVisualization().getAcademicStatus());
    assertEquals("green", result.getProgressVisualization().getStatusColor());
  }

  @Test
  void calculate_emptyTasks_partialData() {
    var result =
        processor.calculate(
            SubjectProgressInput.builder()
                .subjectId("phy-01")
                .completedTasks(List.of())
                .totalTasks(10)
                .academicPerformance(60f)
                .build());

    assertTrue(result.isPartialData());
  }

  @Test
  void calculate_partialDataWithVeryLowProgress_setsNoActivityStatus() {
    var result =
        processor.calculate(
            SubjectProgressInput.builder()
                .subjectId("art-01")
                .completedTasks(List.of())
                .totalTasks(20)
                .academicPerformance(10f)
                .build());

    assertTrue(result.isPartialData());
    assertEquals("NO_ACTIVITY", result.getProgressVisualization().getAcademicStatus());
    assertEquals("gray", result.getProgressVisualization().getStatusColor());
  }

  @Test
  void calculate_midProgress_setsInProgressStatus() {
    var result =
        processor.calculate(
            SubjectProgressInput.builder()
                .subjectId("bio-01")
                .completedTasks(
                    List.of(
                        CompletedTask.builder().taskId("t1").build(),
                        CompletedTask.builder().taskId("t2").build()))
                .totalTasks(5)
                .academicPerformance(50f)
                .build());

    assertEquals("IN_PROGRESS", result.getProgressVisualization().getAcademicStatus());
    assertEquals("orange", result.getProgressVisualization().getStatusColor());
  }

  @Test
  void calculate_highButNotComplete_setsAdvancedStatus() {
    var result =
        processor.calculate(
            SubjectProgressInput.builder()
                .subjectId("chem-02")
                .completedTasks(
                    List.of(
                        CompletedTask.builder().taskId("t1").build(),
                        CompletedTask.builder().taskId("t2").build(),
                        CompletedTask.builder().taskId("t3").build()))
                .totalTasks(4)
                .academicPerformance(80f)
                .build());

    assertEquals("ADVANCED", result.getProgressVisualization().getAcademicStatus());
    assertEquals("blue", result.getProgressVisualization().getStatusColor());
  }

  @Test
  void calculate_lowProgress_setsInitialStatus() {
    var result =
        processor.calculate(
            SubjectProgressInput.builder()
                .subjectId("music-01")
                .completedTasks(List.of(CompletedTask.builder().taskId("t1").build()))
                .totalTasks(4)
                .academicPerformance(0f)
                .build());

    assertFalse(result.isPartialData());
    assertEquals("INITIAL", result.getProgressVisualization().getAcademicStatus());
    assertEquals("yellow", result.getProgressVisualization().getStatusColor());
  }
}
