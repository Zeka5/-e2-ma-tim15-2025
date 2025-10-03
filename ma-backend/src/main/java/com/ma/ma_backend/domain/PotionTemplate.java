package com.ma.ma_backend.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "potion_templates")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PotionTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "power_bonus")
    private Integer powerBonus;

    @Column(name = "is_permanent")
    private Boolean isPermanent;

    @Column(name = "price_multiplier")
    private Double priceMultiplier;

    private String description;

    @Column(name = "icon_url")
    private String iconUrl;
}
