package com.aibert.dosw.infrastructure.adapters.persistence.repository;

import com.aibert.dosw.infrastructure.adapters.persistence.entity.GamificationProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface GamificationJpaRepository extends JpaRepository<GamificationProfileEntity, UUID> {
    Optional<GamificationProfileEntity> findByUserId(UUID userId);
}
