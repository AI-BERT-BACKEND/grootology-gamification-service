package com.aibert.dosw.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.aibert.dosw.application.dto.request.ActionEventRequestDTO;
import com.aibert.dosw.application.dto.request.UserActivityRecordDTO;
import com.aibert.dosw.application.dto.response.PointsResponseDTO;
import com.aibert.dosw.application.mapper.ActivityHistoryMapper;
import com.aibert.dosw.application.mapper.PointsApplicationMapper;
import com.aibert.dosw.domain.exceptions.GamificationProfileNotFoundException;
import com.aibert.dosw.domain.model.user.ActionEvent;
import com.aibert.dosw.domain.model.user.GamificationProfile;
import com.aibert.dosw.domain.model.user.Level;
import com.aibert.dosw.domain.ports.out.GamificationRepositoryPort;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class PointsServiceTest {

  @Mock private GamificationRepositoryPort repository;
  @Spy private ActivityHistoryMapper activityHistoryMapper = Mappers.getMapper(ActivityHistoryMapper.class);
  @Spy private PointsApplicationMapper pointsMapper = Mappers.getMapper(PointsApplicationMapper.class);
  @InjectMocks private PointsService pointsService;

  private final UUID userId = UUID.randomUUID();

  @Test
  void processAcademicEvent_validTask_awardsPoints() {
    when(repository.findByUserId(userId)).thenReturn(Optional.empty());
    when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    PointsResponseDTO response =
        pointsService.processAcademicEvent(
            userId, buildRequest(ActionEvent.TASK_COMPLETED, UUID.randomUUID()));

    assertTrue(response.isPointsUpdated());
    assertEquals(15, response.getXpEarned());
    assertEquals(1, response.getCurrentStreak());
    verify(repository).save(any());
  }

  @Test
  void processAcademicEvent_duplicate_doesNotSave() {
    LocalDateTime completion = LocalDateTime.of(2026, 5, 17, 10, 0);
    UserActivityRecordDTO existing = new UserActivityRecordDTO();
    existing.setActionEvent(ActionEvent.TASK_COMPLETED);
    existing.setCompletionDate(completion);
    UUID duplicateActivityId = UUID.randomUUID();
    existing.setActivityId(duplicateActivityId);

    ActionEventRequestDTO request = new ActionEventRequestDTO();
    request.setActionEvent(ActionEvent.TASK_COMPLETED);
    request.setCompletionDate(completion);
    request.setActivityId(duplicateActivityId);
    request.setUserActivityHistory(List.of(existing));

    GamificationProfile profile =
        GamificationProfile.builder()
            .id(UUID.randomUUID())
            .userId(userId)
            .username("student.points")
            .totalPoints(40)
            .currentStreak(2)
            .globalLevel(Level.NOVATO)
            .achievements(new ArrayList<>())
            .build();

    when(repository.findByUserId(userId)).thenReturn(Optional.of(profile));

    PointsResponseDTO response = pointsService.processAcademicEvent(userId, request);

    assertFalse(response.isPointsUpdated());
    verify(repository, never()).save(any());
  }

  @Test
  void getPointsSummary_profileMissing_throws() {
    when(repository.findByUserId(userId)).thenReturn(Optional.empty());
    assertThrows(GamificationProfileNotFoundException.class, () -> pointsService.getPointsSummary(userId));
  }

  @Test
  void getPointsSummary_returnsCurrentTotals() {
    GamificationProfile profile =
        GamificationProfile.builder()
            .userId(userId)
            .username("student.points")
            .totalPoints(120)
            .currentStreak(4)
            .globalLevel(Level.CONSTANTE)
            .achievements(new ArrayList<>())
            .build();
    when(repository.findByUserId(userId)).thenReturn(Optional.of(profile));

    PointsResponseDTO response = pointsService.getPointsSummary(userId);

    assertEquals(120, response.getTotalPoints());
    assertEquals(4, response.getCurrentStreak());
    assertFalse(response.isPointsUpdated());
  }

  @Test
  void processAcademicEvent_usesAuthenticatedPrincipalWhenProfileUsernameMissing() {
    SecurityContextHolder.getContext()
        .setAuthentication(new UsernamePasswordAuthenticationToken("student.auth", null, List.of()));
    try {
      when(repository.findByUserId(userId)).thenReturn(Optional.empty());
      when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

      pointsService.processAcademicEvent(
          userId, buildRequest(ActionEvent.TASK_COMPLETED, UUID.randomUUID()));

      org.mockito.ArgumentCaptor<GamificationProfile> captor =
          org.mockito.ArgumentCaptor.forClass(GamificationProfile.class);
      verify(repository).save(captor.capture());
      assertEquals("student.auth", captor.getValue().getUsername());
    } finally {
      SecurityContextHolder.clearContext();
    }
  }

  private ActionEventRequestDTO buildRequest(ActionEvent event, UUID activityId) {
    LocalDateTime completion = LocalDateTime.of(2026, 5, 17, 10, 0);
    UserActivityRecordDTO history = new UserActivityRecordDTO();
    history.setActionEvent(ActionEvent.SUBJECT_PROGRESS);
    history.setCompletionDate(completion.minusDays(1));

    ActionEventRequestDTO request = new ActionEventRequestDTO();
    request.setActionEvent(event);
    request.setCompletionDate(completion);
    request.setDueDate(completion.plusHours(12));
    request.setActivityId(activityId);
    request.setUserActivityHistory(List.of(history));
    return request;
  }
}
