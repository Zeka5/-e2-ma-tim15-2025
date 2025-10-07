package com.ma.ma_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserStatisticsDto {
    private Integer activeDaysStreak;
    private Integer tasksCreated;
    private Integer tasksCompleted;
    private Integer tasksPending;
    private Integer tasksCancelled;
    private Integer longestCompletionStreak;
    private Map<String, Integer> tasksByCategory;
    private List<DifficultyDataPoint> averageDifficultyHistory;
    private List<XpDataPoint> xpLast7Days;
    private Integer specialMissionsStarted;
    private Integer specialMissionsCompleted;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DifficultyDataPoint {
        private String date;
        private Float averageDifficulty;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class XpDataPoint {
        private String date;
        private Integer xp;
    }
}
