package com.aibert.dosw.domain.model.achievement;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public final class AchievementCatalog {

  private static final List<AchievementDefinition> DEFINITIONS =
      List.of(
          definition(
              AchievementEvent.TASK_STREAK,
              "Racha de Tareas",
              "badge-task-streak",
              "Completa actividades académicas durante 7 días consecutivos."),
          definition(
              AchievementEvent.PERFECT_SCORE,
              "Puntuación Perfecta",
              "badge-perfect-score",
              "Obtén una calificación perfecta en una evaluación académica."),
          definition(
              AchievementEvent.GOAL_COMPLETED,
              "Meta Alcanzada",
              "badge-goal-completed",
              "Cumple un objetivo académico o meta semanal registrada en la plataforma."),
          definition(
              AchievementEvent.SUBJECT_MASTERY,
              "Dominio de Materia",
              "badge-subject-mastery",
              "Alcanza el 100% de progreso en una materia."),
          definition(
              AchievementEvent.PRODUCTIVITY_STREAK,
              "Productividad Sostenida",
              "badge-productivity-streak",
              "Mantén una racha de productividad de 14 días consecutivos."));

  private AchievementCatalog() {}

  public static List<AchievementDefinition> all() {
    return DEFINITIONS;
  }

  public static Optional<AchievementDefinition> find(AchievementEvent event) {
    return DEFINITIONS.stream().filter(d -> d.getEvent() == event).findFirst();
  }

  public static boolean isValidEvent(AchievementEvent event) {
    return event != null && Arrays.stream(AchievementEvent.values()).anyMatch(e -> e == event);
  }

  private static AchievementDefinition definition(
      AchievementEvent event, String name, String icon, String description) {
    return AchievementDefinition.builder()
        .event(event)
        .name(name)
        .icon(icon)
        .description(description)
        .build();
  }
}
