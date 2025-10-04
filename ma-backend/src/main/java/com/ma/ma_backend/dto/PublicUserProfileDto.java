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
public class PublicUserProfileDto {
    private Long id;
    private String username;
    private Integer avatarId;
    private Integer level;
    private String title;
    private Integer experiencePoints;
    private String qrCode;
    private Integer badgeCount;
    private List<BadgeDto> badges;
}
