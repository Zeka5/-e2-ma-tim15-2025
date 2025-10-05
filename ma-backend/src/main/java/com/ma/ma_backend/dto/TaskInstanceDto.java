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
public class TaskInstanceDto {
    private Long id;
    private Long taskId;
    private String taskTitle;
    private String taskDescription;
    private Long categoryId;
    private String categoryName;
    private String categoryColor;
    private String difficulty;
    private String importance;
    private String status;
    private LocalDateTime startDate;
    private LocalDateTime completedAt;
    private Boolean xpAwarded;
    private Integer xpAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
