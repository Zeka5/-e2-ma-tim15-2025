package com.ma.ma_backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "guilds")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Guild {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader_id", nullable = false, unique = true)
    private User leader;

    @OneToMany(mappedBy = "currentGuild")
    private Set<User> members = new HashSet<>();

    @Column(name = "has_active_mission")
    private Boolean hasActiveMission = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
