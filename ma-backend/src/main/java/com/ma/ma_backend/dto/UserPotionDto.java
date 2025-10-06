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
public class UserPotionDto {
    private Long id;
    private Long potionTemplateId;
    private String name;
    private Integer powerBonus;
    private Boolean isPermanent;
    private Integer quantity;
    private Boolean isActivated;
    private LocalDateTime acquiredAt;
    private String description;
    private String iconUrl;
}
