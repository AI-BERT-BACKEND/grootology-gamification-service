package com.aibert.dosw.domain.model.subject;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SubjectProgressRules {

  public static final float MIN_PERCENTAGE = 0f;
  public static final float MAX_PERCENTAGE = 100f;
  public static final int XP_PER_COMPLETED_TASK = 10;
  public static final int MIN_TASKS_FOR_FULL_PROGRESS = 1;
  public static final float PERFORMANCE_WEIGHT = 0.3f;
  public static final float TASKS_WEIGHT = 0.7f;

  public static final int SUBJECT_XP_NOVATO = 0;
  public static final int SUBJECT_XP_CONSTANTE = 50;
  public static final int SUBJECT_XP_COMPROMETIDO = 150;
  public static final int SUBJECT_XP_AVANZADO = 300;
  public static final int SUBJECT_XP_MAESTRO = 500;
}
