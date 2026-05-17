package com.aibert.dosw.infrastructure.adapters.persistence.repository;

import com.aibert.dosw.infrastructure.adapters.persistence.entity.SubjectProgressEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubjectProgressJpaRepository extends JpaRepository<SubjectProgressEntity, UUID> {
  List<SubjectProgressEntity> findByUserId(UUID userId);

  Optional<SubjectProgressEntity> findByUserIdAndSubjectId(UUID userId, String subjectId);
}
