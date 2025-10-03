package com.ma.ma_backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserGameStatsDto {
    private Long id;
    private Integer level;
    private String title;
    private Integer powerPoints;
    private Integer experiencePoints;
    private Integer coins;
    private String qrCode;
    private Integer badgeCount;
    private List<BadgeDto> badges;
}
