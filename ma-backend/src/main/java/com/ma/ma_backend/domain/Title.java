package com.ma.ma_backend.domain;

public enum Title {
    NOVICE("Novice"),
    APPRENTICE("Apprentice"),
    ADVENTURER("Adventurer"),
    WARRIOR("Warrior"),
    CHAMPION("Champion"),
    MASTER("Master"),
    LEGEND("Legend"),
    MYTHIC("Mythic");

    private final String displayName;

    Title(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
