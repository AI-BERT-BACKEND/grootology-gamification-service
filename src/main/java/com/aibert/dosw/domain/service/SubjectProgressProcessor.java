package com.aibert.dosw.domain.service;

import com.aibert.dosw.domain.model.subject.*;
import com.aibert.dosw.domain.model.user.Level;

import java.util.List;

/** Lógica de negocio para visualización de progreso por materia (AIB-38). */
public class SubjectProgressProcessor {

  public SubjectProgressResult calculate(SubjectProgressInput input) {
    if (!isValidSubject(input)) {
      return invalidResult(input, "Invalid academic information for subject (FA-04).");
    }

    List<CompletedTask> completedTasks = input.getCompletedTasks();
    boolean partialData = isPartialData(input, completedTasks);

    int xpEarned = calculateXp(input, completedTasks);
    float progressPercentage = calculateProgressPercentage(input, completedTasks);
    Level subjectLevel = calculateSubjectLevel(xpEarned);
    ProgressVisualization visualization =
        buildVisualization(input, completedTasks, progressPercentage, xpEarned, partialData);

    return SubjectProgressResult.builder()
        .subjectId(input.getSubjectId())
        .subjectName(input.getSubjectName())
        .subjectProgressPercentage(progressPercentage)
        .subjectLevel(subjectLevel)
        .xpEarned(xpEarned)
        .academicPerformance(normalizePerformance(input.getAcademicPerformance()))
        .progressVisualization(visualization)
        .partialData(partialData)
        .valid(true)
        .message(partialData ? "Partial progress: insufficient academic activity (FA-02)." : null)
        .build();
  }

  private boolean isValidSubject(SubjectProgressInput input) {
    return input != null
        && input.getSubjectId() != null
        && !input.getSubjectId().isBlank()
        && input.getAcademicPerformance() != null
        && input.getAcademicPerformance() >= 0;
  }

  private boolean isPartialData(SubjectProgressInput input, List<CompletedTask> completedTasks) {
    if (completedTasks == null || completedTasks.isEmpty()) {
      return true;
    }
    int total = resolveTotalTasks(input, completedTasks.size());
    return completedTasks.size() < SubjectProgressRules.MIN_TASKS_FOR_FULL_PROGRESS
        || completedTasks.size() < total * 0.25f;
  }

  private int calculateXp(SubjectProgressInput input, List<CompletedTask> completedTasks) {
    if (completedTasks == null || completedTasks.isEmpty()) {
      return 0;
    }
    int fromTasks =
        completedTasks.stream()
            .mapToInt(
                task ->
                    task.getXpValue() > 0
                        ? task.getXpValue()
                        : SubjectProgressRules.XP_PER_COMPLETED_TASK)
            .sum();
    if (input.getXpEarned() != null && input.getXpEarned() > 0) {
      return Math.max(fromTasks, input.getXpEarned());
    }
    return fromTasks;
  }

  private float calculateProgressPercentage(
      SubjectProgressInput input, List<CompletedTask> completedTasks) {
    int completedCount = completedTasks == null ? 0 : completedTasks.size();
    int totalTasks = resolveTotalTasks(input, completedCount);
    float taskRatio = totalTasks > 0 ? (float) completedCount / totalTasks : 0f;
    float performance = normalizePerformance(input.getAcademicPerformance()) / 100f;

    float raw =
        (taskRatio * SubjectProgressRules.TASKS_WEIGHT
                + performance * SubjectProgressRules.PERFORMANCE_WEIGHT)
            * 100f;
    return clampPercentage(raw);
  }

  private int resolveTotalTasks(SubjectProgressInput input, int completedCount) {
    if (input.getTotalTasks() != null && input.getTotalTasks() > 0) {
      return input.getTotalTasks();
    }
    return Math.max(completedCount, 1);
  }

  private float normalizePerformance(Float performance) {
    if (performance == null) {
      return 0f;
    }
    return clampPercentage(performance);
  }

  private float clampPercentage(float value) {
    return Math.max(
        SubjectProgressRules.MIN_PERCENTAGE,
        Math.min(SubjectProgressRules.MAX_PERCENTAGE, value));
  }

  private Level calculateSubjectLevel(int xpEarned) {
    if (xpEarned >= SubjectProgressRules.SUBJECT_XP_MAESTRO) {
      return Level.MAESTRO_DEL_TIEMPO;
    }
    if (xpEarned >= SubjectProgressRules.SUBJECT_XP_AVANZADO) {
      return Level.AVANZADO;
    }
    if (xpEarned >= SubjectProgressRules.SUBJECT_XP_COMPROMETIDO) {
      return Level.COMPROMETIDO;
    }
    if (xpEarned >= SubjectProgressRules.SUBJECT_XP_CONSTANTE) {
      return Level.CONSTANTE;
    }
    return Level.NOVATO;
  }

  private ProgressVisualization buildVisualization(
      SubjectProgressInput input,
      List<CompletedTask> completedTasks,
      float progressPercentage,
      int xpEarned,
      boolean partialData) {
    int completedCount = completedTasks == null ? 0 : completedTasks.size();
    int totalTasks = resolveTotalTasks(input, completedCount);
    String status = resolveAcademicStatus(progressPercentage, partialData);
    return ProgressVisualization.builder()
        .progressBarPercent(progressPercentage)
        .xpDisplay(xpEarned)
        .academicStatus(status)
        .statusColor(resolveStatusColor(status))
        .tasksCompletedLabel(completedCount + "/" + totalTasks + " tasks")
        .build();
  }

  private String resolveAcademicStatus(float percentage, boolean partialData) {
    if (partialData && percentage < 25f) {
      return "NO_ACTIVITY";
    }
    if (percentage >= 100f) {
      return "COMPLETED";
    }
    if (percentage >= 75f) {
      return "ADVANCED";
    }
    if (percentage >= 40f) {
      return "IN_PROGRESS";
    }
    return "INITIAL";
  }

  private String resolveStatusColor(String status) {
    return switch (status) {
      case "COMPLETED" -> "green";
      case "ADVANCED" -> "blue";
      case "IN_PROGRESS" -> "orange";
      case "NO_ACTIVITY" -> "gray";
      default -> "yellow";
    };
  }

  private SubjectProgressResult invalidResult(SubjectProgressInput input, String message) {
    String subjectId = input != null ? input.getSubjectId() : null;
    String subjectName = input != null ? input.getSubjectName() : null;
    return SubjectProgressResult.builder()
        .subjectId(subjectId)
        .subjectName(subjectName)
        .subjectProgressPercentage(0f)
        .subjectLevel(Level.NOVATO)
        .xpEarned(0)
        .academicPerformance(0f)
        .progressVisualization(
            ProgressVisualization.builder()
                .progressBarPercent(0f)
                .xpDisplay(0)
                .academicStatus("UNAVAILABLE")
                .statusColor("gray")
                .tasksCompletedLabel("0/0 tasks")
                .build())
        .partialData(true)
        .valid(false)
        .message(message)
        .build();
  }
}
