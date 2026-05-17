package com.aibert.dosw.infrastructure.adapters.persistence.entity;

import com.aibert.dosw.domain.model.user.Level;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "gamification_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GamificationProfileEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false, unique = true)
  private UUID userId;

  @Column(nullable = false)
  private int totalPoints;

  @Column(nullable = false)
  private int currentStreak;

  private LocalDate lastActivityDate;

  @Enumerated(EnumType.STRING)
  @Column(name = "global_level", nullable = false)
  private Level globalLevel;

  @ElementCollection
  @CollectionTable(name = "user_achievements", joinColumns = @JoinColumn(name = "profile_id"))
  private List<UnlockedAchievementEmbeddable> achievements;
}
