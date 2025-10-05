package com.ma.ma_backend.domain;

public enum TaskImportance {
    NORMAL(1),
    IMPORTANT(3),
    EXTREMELY_IMPORTANT(10),
    SPECIAL(100); // Special tasks have custom XP logic

    private final int xp;

    TaskImportance(int xp) {
        this.xp = xp;
    }

    public int getXp() {
        return xp;
    }
}
