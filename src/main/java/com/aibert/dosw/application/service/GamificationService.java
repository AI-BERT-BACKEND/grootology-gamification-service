package com.aibert.dosw.application.service;

import com.aibert.dosw.application.dto.request.ActionEventRequestDTO;
import com.aibert.dosw.application.dto.response.GamificationResponseDTO;
import com.aibert.dosw.domain.exceptions.GamificationProfileNotFoundException;
import com.aibert.dosw.domain.model.user.*;
import com.aibert.dosw.domain.ports.in.GamificationUseCase;
import com.aibert.dosw.domain.ports.out.GamificationRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GamificationService implements GamificationUseCase {

    private final GamificationRepositoryPort repository;

    @Override
    public GamificationResponseDTO processEvent(UUID userId, ActionEventRequestDTO request) {
        GamificationProfile profile = repository.findByUserId(userId)
                .orElse(GamificationProfile.builder()
                        .userId(userId)
                        .totalPoints(0)
                        .currentStreak(0)
                        .level(Level.NOVATO)
                        .badges(new ArrayList<>())
                        .build());

        int points = profile.getTotalPoints();
        int streak = profile.getCurrentStreak();
        List<Badge> badges = new ArrayList<>(profile.getBadges());
        Badge newBadge = null;

        switch (request.getEvent()) {
            case TASK_COMPLETED_ON_TIME -> {
                points += 10;
                streak++;
                if (badges.isEmpty() && !badges.contains(Badge.PRIMERA_TAREA)) {
                    badges.add(Badge.PRIMERA_TAREA);
                    newBadge = Badge.PRIMERA_TAREA;
                }
                if (streak == 7 && !badges.contains(Badge.RACHA_7_DIAS)) {
                    badges.add(Badge.RACHA_7_DIAS);
                    newBadge = Badge.RACHA_7_DIAS;
                }
                if (streak == 30 && !badges.contains(Badge.RACHA_30_DIAS)) {
                    badges.add(Badge.RACHA_30_DIAS);
                    newBadge = Badge.RACHA_30_DIAS;
                }
            }
            case TASK_COMPLETED_LATE -> streak = 0;
            case STREAK_MAINTAINED -> points += 5;
            case WEEKLY_GOAL_MET -> {
                points += 20;
                if (!badges.contains(Badge.META_SEMANAL)) {
                    badges.add(Badge.META_SEMANAL);
                    newBadge = Badge.META_SEMANAL;
                }
            }
        }

        Level previousLevel = profile.getLevel();
        Level newLevel = calculateLevel(points);

        GamificationProfile updated = GamificationProfile.builder()
                .id(profile.getId())
                .userId(userId)
                .totalPoints(points)
                .currentStreak(streak)
                .level(newLevel)
                .badges(badges)
                .build();

        repository.save(updated);

        return GamificationResponseDTO.builder()
                .totalPoints(points)
                .currentStreak(streak)
                .level(newLevel)
                .badges(badges)
                .newBadge(newBadge)
                .leveledUp(!newLevel.equals(previousLevel))
                .build();
    }

    @Override
    public GamificationResponseDTO getProgress(UUID userId) {
        GamificationProfile profile = repository.findByUserId(userId)
                .orElseThrow(GamificationProfileNotFoundException::new);

        return GamificationResponseDTO.builder()
                .totalPoints(profile.getTotalPoints())
                .currentStreak(profile.getCurrentStreak())
                .level(profile.getLevel())
                .badges(profile.getBadges())
                .build();
    }

    private Level calculateLevel(int points) {
        if (points >= 500) return Level.MAESTRO_DEL_TIEMPO;
        if (points >= 300) return Level.AVANZADO;
        if (points >= 150) return Level.COMPROMETIDO;
        if (points >= 50) return Level.CONSTANTE;
        return Level.NOVATO;
    }
}
