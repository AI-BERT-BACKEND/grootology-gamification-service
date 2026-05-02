package com.aibert.dosw.domain.model.user;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class GamificationProfile {
    private UUID id;
    private UUID userId;
    private int totalPoints;
    private int currentStreak;
    private Level level;
    private List<Badge> badges;
}
