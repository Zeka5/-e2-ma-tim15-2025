package com.ma.ma_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserClothingDto {
    private Long id;
    private Long clothingTemplateId;
    private String name;
    private String type;
    private Integer accumulatedBonus;
    private Integer battlesRemaining;
    private Boolean isActive;
    private LocalDateTime acquiredAt;
    private String description;
    private String iconUrl;
}
