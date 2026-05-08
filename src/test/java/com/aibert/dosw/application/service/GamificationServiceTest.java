package com.aibert.dosw.application.service;

import com.aibert.dosw.application.dto.request.ActionEventRequestDTO;
import com.aibert.dosw.application.dto.response.GamificationResponseDTO;
import com.aibert.dosw.domain.exceptions.GamificationProfileNotFoundException;
import com.aibert.dosw.domain.model.user.*;
import com.aibert.dosw.domain.ports.out.GamificationRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GamificationServiceTest {

    @Mock private GamificationRepositoryPort repository;
    @InjectMocks private GamificationService gamificationService;

    private final UUID userId = UUID.randomUUID();

    @Test
    void processEvent_tareaCompletadaATiempo_sumaPuntos() {
        ActionEventRequestDTO request = mock(ActionEventRequestDTO.class);
        when(request.getEvent()).thenReturn(ActionEvent.TASK_COMPLETED_ON_TIME);
        when(repository.findByUserId(userId)).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        GamificationResponseDTO response = gamificationService.processEvent(userId, request);

        assertEquals(10, response.getTotalPoints());
        assertEquals(1, response.getCurrentStreak());
    }

    @Test
    void processEvent_tareaCompletadaTarde_resetRacha() {
        ActionEventRequestDTO request = mock(ActionEventRequestDTO.class);
        when(request.getEvent()).thenReturn(ActionEvent.TASK_COMPLETED_LATE);

        GamificationProfile profile = GamificationProfile.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .totalPoints(50)
                .currentStreak(5)
                .level(Level.CONSTANTE)
                .badges(new ArrayList<>())
                .build();

        when(repository.findByUserId(userId)).thenReturn(Optional.of(profile));
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        GamificationResponseDTO response = gamificationService.processEvent(userId, request);

        assertEquals(0, response.getCurrentStreak());
        assertEquals(50, response.getTotalPoints());
    }

    @Test
    void processEvent_metaSemanal_suma20Puntos() {
        ActionEventRequestDTO request = mock(ActionEventRequestDTO.class);
        when(request.getEvent()).thenReturn(ActionEvent.WEEKLY_GOAL_MET);
        when(repository.findByUserId(userId)).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        GamificationResponseDTO response = gamificationService.processEvent(userId, request);

        assertEquals(20, response.getTotalPoints());
        assertTrue(response.getBadges().contains(Badge.META_SEMANAL));
    }

    @Test
    void getProgress_perfilNoExiste_lanzaException() {
        when(repository.findByUserId(userId)).thenReturn(Optional.empty());
        assertThrows(GamificationProfileNotFoundException.class,
                () -> gamificationService.getProgress(userId));
    }

    @Test
    void getProgress_perfilExiste_retornaDatos() {
        GamificationProfile profile = GamificationProfile.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .totalPoints(150)
                .currentStreak(3)
                .level(Level.COMPROMETIDO)
                .badges(new ArrayList<>())
                .build();

        when(repository.findByUserId(userId)).thenReturn(Optional.of(profile));

        GamificationResponseDTO response = gamificationService.getProgress(userId);

        assertEquals(150, response.getTotalPoints());
        assertEquals(Level.COMPROMETIDO, response.getLevel());
    }

    @Test
    void processEvent_streakMaintained_suma5Puntos() {
        ActionEventRequestDTO request = mock(ActionEventRequestDTO.class);
        when(request.getEvent()).thenReturn(ActionEvent.STREAK_MAINTAINED);
        when(repository.findByUserId(userId)).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        GamificationResponseDTO response = gamificationService.processEvent(userId, request);

        assertEquals(5, response.getTotalPoints());
    }

    @Test
    void processEvent_racha7Dias_otorgaBadge() {
        ActionEventRequestDTO request = mock(ActionEventRequestDTO.class);
        when(request.getEvent()).thenReturn(ActionEvent.TASK_COMPLETED_ON_TIME);

        GamificationProfile profile = GamificationProfile.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .totalPoints(60)
                .currentStreak(6)
                .level(Level.CONSTANTE)
                .badges(new ArrayList<>())
                .build();

        when(repository.findByUserId(userId)).thenReturn(Optional.of(profile));
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        GamificationResponseDTO response = gamificationService.processEvent(userId, request);

        assertEquals(7, response.getCurrentStreak());
        assertTrue(response.getBadges().contains(Badge.RACHA_7_DIAS));
    }

    @Test
    void processEvent_racha30Dias_otorgaBadge() {
        ActionEventRequestDTO request = mock(ActionEventRequestDTO.class);
        when(request.getEvent()).thenReturn(ActionEvent.TASK_COMPLETED_ON_TIME);

        GamificationProfile profile = GamificationProfile.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .totalPoints(290)
                .currentStreak(29)
                .level(Level.AVANZADO)
                .badges(new ArrayList<>(List.of(Badge.RACHA_7_DIAS)))
                .build();

        when(repository.findByUserId(userId)).thenReturn(Optional.of(profile));
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        GamificationResponseDTO response = gamificationService.processEvent(userId, request);

        assertEquals(30, response.getCurrentStreak());
        assertTrue(response.getBadges().contains(Badge.RACHA_30_DIAS));
    }

    @Test
    void processEvent_subidaDeNivel_detectaLevelUp() {
        ActionEventRequestDTO request = mock(ActionEventRequestDTO.class);
        when(request.getEvent()).thenReturn(ActionEvent.TASK_COMPLETED_ON_TIME);

        GamificationProfile profile = GamificationProfile.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .totalPoints(49)
                .currentStreak(0)
                .level(Level.NOVATO)
                .badges(new ArrayList<>(List.of(Badge.PRIMERA_TAREA)))
                .build();

        when(repository.findByUserId(userId)).thenReturn(Optional.of(profile));
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        GamificationResponseDTO response = gamificationService.processEvent(userId, request);

        assertTrue(response.isLeveledUp());
        assertEquals(Level.CONSTANTE, response.getLevel());
    }

    @Test
    void processEvent_metaSemanalYaBadge_noAgregaDuplicado() {
        ActionEventRequestDTO request = mock(ActionEventRequestDTO.class);
        when(request.getEvent()).thenReturn(ActionEvent.WEEKLY_GOAL_MET);

        GamificationProfile profile = GamificationProfile.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .totalPoints(100)
                .currentStreak(0)
                .level(Level.CONSTANTE)
                .badges(new ArrayList<>(List.of(Badge.META_SEMANAL)))
                .build();

        when(repository.findByUserId(userId)).thenReturn(Optional.of(profile));
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        GamificationResponseDTO response = gamificationService.processEvent(userId, request);

        assertEquals(1, response.getBadges().stream().filter(b -> b == Badge.META_SEMANAL).count());
    }
}
