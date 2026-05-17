package com.aibert.dosw.domain.model.achievement;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class AchievementCatalogTest {

  @Test
  void all_containsFiveAchievements() {
    assertEquals(5, AchievementCatalog.all().size());
  }

  @Test
  void find_returnsDefinition() {
    assertTrue(AchievementCatalog.find(AchievementEvent.PERFECT_SCORE).isPresent());
  }

  @Test
  void isValidEvent_acceptsKnownValues() {
    assertTrue(AchievementCatalog.isValidEvent(AchievementEvent.TASK_STREAK));
  }
}
