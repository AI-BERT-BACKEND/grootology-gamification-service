package com.aibert.dosw.infrastructure.adapters.persistence.entity;

import com.aibert.dosw.domain.model.user.Badge;
import com.aibert.dosw.domain.model.user.Level;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "gamification_profiles")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Level level;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_badges", joinColumns = @JoinColumn(name = "profile_id"))
    @Column(name = "badge")
    private List<Badge> badges;
}
