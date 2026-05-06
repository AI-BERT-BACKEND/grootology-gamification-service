package com.aibert.dosw.domain.ports.out;

import com.aibert.dosw.domain.model.user.GamificationProfile;

import java.util.Optional;
import java.util.UUID;

public interface GamificationRepositoryPort {
    GamificationProfile save(GamificationProfile profile);
    Optional<GamificationProfile> findByUserId(UUID userId);
}
