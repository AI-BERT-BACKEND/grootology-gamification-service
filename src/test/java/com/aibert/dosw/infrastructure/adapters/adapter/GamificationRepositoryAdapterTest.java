package com.aibert.dosw.infrastructure.adapters.adapter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.aibert.dosw.domain.model.user.GamificationProfile;
import com.aibert.dosw.domain.model.user.Level;
import com.aibert.dosw.infrastructure.adapters.persistence.entity.GamificationProfileEntity;
import com.aibert.dosw.infrastructure.adapters.persistence.mapper.GamificationPersistenceMapper;
import com.aibert.dosw.infrastructure.adapters.persistence.repository.GamificationJpaRepository;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GamificationRepositoryAdapterTest {

  @Mock private GamificationJpaRepository jpaRepository;
  @Spy private GamificationPersistenceMapper mapper = Mappers.getMapper(GamificationPersistenceMapper.class);
  @InjectMocks private GamificationRepositoryAdapter adapter;

  @Test
  void save_persistsProfile() {
    GamificationProfile profile =
        GamificationProfile.builder()
            .userId(UUID.randomUUID())
            .username("student.repo")
            .totalPoints(10)
            .currentStreak(1)
            .globalLevel(Level.NOVATO)
            .achievements(new ArrayList<>())
            .build();

    when(jpaRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    GamificationProfile saved = adapter.save(profile);

    assertEquals(10, saved.getTotalPoints());
    verify(jpaRepository).save(any(GamificationProfileEntity.class));
  }

  @Test
  void findByUserId_returnsMappedProfile() {
    UUID userId = UUID.randomUUID();
    GamificationProfileEntity entity =
        GamificationProfileEntity.builder()
            .userId(userId)
            .username("student.repo")
            .totalPoints(80)
            .currentStreak(2)
            .globalLevel(Level.CONSTANTE)
            .achievements(new ArrayList<>())
            .build();

    when(jpaRepository.findByUserId(userId)).thenReturn(Optional.of(entity));

    Optional<GamificationProfile> result = adapter.findByUserId(userId);

    assertTrue(result.isPresent());
    assertEquals(80, result.get().getTotalPoints());
  }
}
