package com.aibert.dosw.infrastructure.kafka;

public record LevelUpEvent(
    String userId,
    int newLevelNumber,
    int previousLevelNumber,
    String levelName) {}
