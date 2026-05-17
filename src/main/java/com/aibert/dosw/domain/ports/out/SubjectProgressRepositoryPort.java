package com.aibert.dosw.domain.ports.out;

import com.aibert.dosw.domain.model.subject.SubjectProgressSnapshot;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubjectProgressRepositoryPort {
  SubjectProgressSnapshot save(SubjectProgressSnapshot snapshot);

  List<SubjectProgressSnapshot> findByUserId(UUID userId);

  Optional<SubjectProgressSnapshot> findByUserIdAndSubjectId(UUID userId, String subjectId);
}
