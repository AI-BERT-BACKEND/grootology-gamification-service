package com.aibert.dosw.application.dto.response;

import com.aibert.dosw.domain.model.user.Badge;
import com.aibert.dosw.domain.model.user.Level;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GamificationResponseDTO {
    private int totalPoints;
    private int currentStreak;
    private Level level;
    private List<Badge> badges;
    private Badge newBadge;
    private boolean leveledUp;
}
