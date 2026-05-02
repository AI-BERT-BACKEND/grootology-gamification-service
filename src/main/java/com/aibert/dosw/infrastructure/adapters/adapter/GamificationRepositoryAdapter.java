package com.aibert.dosw.infrastructure.adapters.adapter;

import com.aibert.dosw.domain.model.user.GamificationProfile;
import com.aibert.dosw.domain.ports.out.GamificationRepositoryPort;
import com.aibert.dosw.infrastructure.adapters.persistence.mapper.GamificationPersistenceMapper;
import com.aibert.dosw.infrastructure.adapters.persistence.repository.GamificationJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GamificationRepositoryAdapter implements GamificationRepositoryPort {

    private final GamificationJpaRepository jpaRepository;
    private final GamificationPersistenceMapper mapper;

    @Override
    public GamificationProfile save(GamificationProfile profile) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(profile)));
    }

    @Override
    public Optional<GamificationProfile> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId).map(mapper::toDomain);
    }
}
