package com.ma.ma_backend.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "clothing_templates")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClothingTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "clothing_type", nullable = false)
    private ClothingType clothingType;

    @Column(name = "bonus_percentage")
    private Integer bonusPercentage;

    @Column(name = "price_multiplier")
    private Double priceMultiplier;

    private String description;

    @Column(name = "icon_url")
    private String iconUrl;
}
