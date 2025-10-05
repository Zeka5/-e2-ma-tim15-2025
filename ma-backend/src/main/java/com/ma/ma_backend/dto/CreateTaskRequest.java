package com.ma.ma_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateTaskRequest {
    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Category is required")
    private Long categoryId;

    @NotBlank(message = "Difficulty is required")
    private String difficulty;

    @NotBlank(message = "Importance is required")
    private String importance;

    @NotNull(message = "Is recurring flag is required")
    private Boolean isRecurring = false;

    private Integer recurrenceInterval;
    private String recurrenceUnit;

    @NotNull(message = "Start date is required")
    private LocalDateTime startDate;

    private LocalDateTime endDate;
}
