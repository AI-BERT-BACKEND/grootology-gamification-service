package com.aibert.dosw.infrastructure.adapters.persistence.entity;

import com.aibert.dosw.domain.model.user.Level;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
    name = "subject_progress",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "subject_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubjectProgressEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "user_id", nullable = false)
  private UUID userId;

  @Column(name = "subject_id", nullable = false)
  private String subjectId;

  private String subjectName;

  @Column(nullable = false)
  private float subjectProgressPercentage;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Level subjectLevel;

  @Column(nullable = false)
  private int xpEarned;

  @Column(nullable = false)
  private float academicPerformance;

  @Column(nullable = false)
  private float progressBarPercent;

  @Column(nullable = false)
  private int xpDisplay;

  private String academicStatus;
  private String statusColor;
  private String tasksCompletedLabel;

  @Column(nullable = false)
  private boolean partialData;
}
