package com.ma.ma_backend.domain;

public enum GuildBossBadge {
    BRONZE("Bronze Champion", 1, 5),
    SILVER("Silver Champion", 6, 10),
    GOLD("Gold Champion", 11, 15),
    PLATINUM("Platinum Champion", 16, 20),
    DIAMOND("Diamond Champion", 21, Integer.MAX_VALUE);

    private final String title;
    private final int minTasks;
    private final int maxTasks;

    GuildBossBadge(String title, int minTasks, int maxTasks) {
        this.title = title;
        this.minTasks = minTasks;
        this.maxTasks = maxTasks;
    }

    public String getTitle() {
        return title;
    }

    public int getMinTasks() {
        return minTasks;
    }

    public int getMaxTasks() {
        return maxTasks;
    }

    public static GuildBossBadge fromTasksCompleted(int tasksCompleted) {
        for (GuildBossBadge badge : values()) {
            if (tasksCompleted >= badge.minTasks && tasksCompleted <= badge.maxTasks) {
                return badge;
            }
        }
        return BRONZE; // Default badge
    }
}
