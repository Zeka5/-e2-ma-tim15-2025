package com.ma.ma_backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BossDto {
    private Long id;
    private Integer level;
    private Integer maxHp;
    private Integer coinReward;
    private String name;
    private String description;
    private String imageUrl;
}
