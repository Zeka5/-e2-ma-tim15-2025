package com.example.ma_mobile.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;

public class UserStatistics {
    @SerializedName("activeDaysStreak")
    private Integer activeDaysStreak;

    @SerializedName("tasksCreated")
    private Integer tasksCreated;

    @SerializedName("tasksCompleted")
    private Integer tasksCompleted;

    @SerializedName("tasksPending")
    private Integer tasksPending;

    @SerializedName("tasksCancelled")
    private Integer tasksCancelled;

    @SerializedName("longestCompletionStreak")
    private Integer longestCompletionStreak;

    @SerializedName("tasksByCategory")
    private Map<String, Integer> tasksByCategory;

    @SerializedName("averageDifficultyHistory")
    private List<DifficultyDataPoint> averageDifficultyHistory;

    @SerializedName("xpLast7Days")
    private List<XpDataPoint> xpLast7Days;

    @SerializedName("specialMissionsStarted")
    private Integer specialMissionsStarted;

    @SerializedName("specialMissionsCompleted")
    private Integer specialMissionsCompleted;

    public UserStatistics() {
    }

    // Getters and Setters
    public Integer getActiveDaysStreak() {
        return activeDaysStreak;
    }

    public void setActiveDaysStreak(Integer activeDaysStreak) {
        this.activeDaysStreak = activeDaysStreak;
    }

    public Integer getTasksCreated() {
        return tasksCreated;
    }

    public void setTasksCreated(Integer tasksCreated) {
        this.tasksCreated = tasksCreated;
    }

    public Integer getTasksCompleted() {
        return tasksCompleted;
    }

    public void setTasksCompleted(Integer tasksCompleted) {
        this.tasksCompleted = tasksCompleted;
    }

    public Integer getTasksPending() {
        return tasksPending;
    }

    public void setTasksPending(Integer tasksPending) {
        this.tasksPending = tasksPending;
    }

    public Integer getTasksCancelled() {
        return tasksCancelled;
    }

    public void setTasksCancelled(Integer tasksCancelled) {
        this.tasksCancelled = tasksCancelled;
    }

    public Integer getLongestCompletionStreak() {
        return longestCompletionStreak;
    }

    public void setLongestCompletionStreak(Integer longestCompletionStreak) {
        this.longestCompletionStreak = longestCompletionStreak;
    }

    public Map<String, Integer> getTasksByCategory() {
        return tasksByCategory;
    }

    public void setTasksByCategory(Map<String, Integer> tasksByCategory) {
        this.tasksByCategory = tasksByCategory;
    }

    public List<DifficultyDataPoint> getAverageDifficultyHistory() {
        return averageDifficultyHistory;
    }

    public void setAverageDifficultyHistory(List<DifficultyDataPoint> averageDifficultyHistory) {
        this.averageDifficultyHistory = averageDifficultyHistory;
    }

    public List<XpDataPoint> getXpLast7Days() {
        return xpLast7Days;
    }

    public void setXpLast7Days(List<XpDataPoint> xpLast7Days) {
        this.xpLast7Days = xpLast7Days;
    }

    public Integer getSpecialMissionsStarted() {
        return specialMissionsStarted;
    }

    public void setSpecialMissionsStarted(Integer specialMissionsStarted) {
        this.specialMissionsStarted = specialMissionsStarted;
    }

    public Integer getSpecialMissionsCompleted() {
        return specialMissionsCompleted;
    }

    public void setSpecialMissionsCompleted(Integer specialMissionsCompleted) {
        this.specialMissionsCompleted = specialMissionsCompleted;
    }

    // Inner classes for data points
    public static class DifficultyDataPoint {
        @SerializedName("date")
        private String date;

        @SerializedName("averageDifficulty")
        private Float averageDifficulty;

        public DifficultyDataPoint(String date, Float averageDifficulty) {
            this.date = date;
            this.averageDifficulty = averageDifficulty;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public Float getAverageDifficulty() {
            return averageDifficulty;
        }

        public void setAverageDifficulty(Float averageDifficulty) {
            this.averageDifficulty = averageDifficulty;
        }
    }

    public static class XpDataPoint {
        @SerializedName("date")
        private String date;

        @SerializedName("xp")
        private Integer xp;

        public XpDataPoint(String date, Integer xp) {
            this.date = date;
            this.xp = xp;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public Integer getXp() {
            return xp;
        }

        public void setXp(Integer xp) {
            this.xp = xp;
        }
    }
}
