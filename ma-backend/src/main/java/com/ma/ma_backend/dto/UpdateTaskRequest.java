package com.ma.ma_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTaskRequest {
    private String title;
    private String description;
    private Long categoryId;
    private String difficulty;
    private String importance;
    private Boolean isRecurring;
    private Integer recurrenceInterval;
    private String recurrenceUnit;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
