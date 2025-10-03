package com.ma.ma_backend.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "weapon_templates")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeaponTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "weapon_type", nullable = false)
    private WeaponType weaponType;

    @Column(name = "base_bonus_percentage")
    private Double baseBonusPercentage;

    @Column(name = "upgrade_price_multiplier")
    private Double upgradePriceMultiplier;

    private String description;

    @Column(name = "icon_url")
    private String iconUrl;
}
