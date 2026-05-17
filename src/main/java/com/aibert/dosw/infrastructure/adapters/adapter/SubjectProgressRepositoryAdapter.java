package com.aibert.dosw.infrastructure.adapters.adapter;

import com.aibert.dosw.domain.model.subject.SubjectProgressSnapshot;
import com.aibert.dosw.domain.ports.out.SubjectProgressRepositoryPort;
import com.aibert.dosw.infrastructure.adapters.persistence.entity.SubjectProgressEntity;
import com.aibert.dosw.infrastructure.adapters.persistence.mapper.SubjectProgressPersistenceMapper;
import com.aibert.dosw.infrastructure.adapters.persistence.repository.SubjectProgressJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubjectProgressRepositoryAdapter implements SubjectProgressRepositoryPort {

  private final SubjectProgressJpaRepository jpaRepository;
  private final SubjectProgressPersistenceMapper mapper;

  @Override
  public SubjectProgressSnapshot save(SubjectProgressSnapshot snapshot) {
    SubjectProgressEntity entity =
        jpaRepository
            .findByUserIdAndSubjectId(snapshot.getUserId(), snapshot.getSubjectId())
            .map(existing -> {
              mapper.updateEntity(existing, snapshot);
              return existing;
            })
            .orElseGet(() -> mapper.toEntity(snapshot));

    return mapper.toDomain(jpaRepository.save(entity));
  }

  @Override
  public List<SubjectProgressSnapshot> findByUserId(UUID userId) {
    return jpaRepository.findByUserId(userId).stream().map(mapper::toDomain).toList();
  }

  @Override
  public Optional<SubjectProgressSnapshot> findByUserIdAndSubjectId(UUID userId, String subjectId) {
    return jpaRepository.findByUserIdAndSubjectId(userId, subjectId).map(mapper::toDomain);
  }
}
