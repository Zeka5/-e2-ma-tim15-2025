package com.ma.ma_backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskDto {
    private Long id;
    private String title;
    private String description;
    private Long categoryId;
    private String categoryName;
    private String categoryColor;
    private Long userId;
    private String difficulty;
    private String importance;
    private Integer totalXp;
    private Boolean isRecurring;
    private Integer recurrenceInterval;
    private String recurrenceUnit;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
