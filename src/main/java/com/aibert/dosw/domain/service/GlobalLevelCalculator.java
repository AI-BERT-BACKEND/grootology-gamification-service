package com.aibert.dosw.domain.service;

import com.aibert.dosw.domain.model.subject.SubjectProgressRules;
import com.aibert.dosw.domain.model.user.Level;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GlobalLevelCalculator {

  public static Level fromTotalXp(int totalXp) {
    if (totalXp >= SubjectProgressRules.SUBJECT_XP_MAESTRO) {
      return Level.MAESTRO_DEL_TIEMPO;
    }
    if (totalXp >= SubjectProgressRules.SUBJECT_XP_AVANZADO) {
      return Level.AVANZADO;
    }
    if (totalXp >= SubjectProgressRules.SUBJECT_XP_COMPROMETIDO) {
      return Level.COMPROMETIDO;
    }
    if (totalXp >= SubjectProgressRules.SUBJECT_XP_CONSTANTE) {
      return Level.CONSTANTE;
    }
    return Level.NOVATO;
  }
}
