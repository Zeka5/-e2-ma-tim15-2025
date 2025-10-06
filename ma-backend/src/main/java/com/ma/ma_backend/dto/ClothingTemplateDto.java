package com.ma.ma_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClothingTemplateDto {
    private Long id;
    private String name;
    private String type;
    private Integer bonus;
    private Integer calculatedPrice;
    private String description;
    private String iconUrl;
}
