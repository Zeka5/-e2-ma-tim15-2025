package com.ma.ma_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PotionTemplateDto {
    private Long id;
    private String name;
    private Integer powerBonus;
    private Boolean isPermanent;
    private Integer calculatedPrice;
    private String description;
    private String iconUrl;
}
