package com.aibert.dosw.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.aibert.dosw.domain.model.user.Level;
import org.junit.jupiter.api.Test;

class GlobalLevelCalculatorTest {

  @Test
  void fromTotalXp_mapsLevels() {
    assertEquals(Level.NOVATO, GlobalLevelCalculator.fromTotalXp(10));
    assertEquals(Level.CONSTANTE, GlobalLevelCalculator.fromTotalXp(75));
    assertEquals(Level.MAESTRO_DEL_TIEMPO, GlobalLevelCalculator.fromTotalXp(550));
  }
}
