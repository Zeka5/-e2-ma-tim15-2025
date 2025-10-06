package com.example.ma_mobile.models;

import com.google.gson.annotations.SerializedName;

public class BattleStatsPreview {
    @SerializedName("userPowerPoints")
    private Integer userPowerPoints;

    @SerializedName("successRate")
    private Double successRate;

    @SerializedName("maxAttacks")
    private Integer maxAttacks;

    public BattleStatsPreview() {
    }

    public Integer getUserPowerPoints() {
        return userPowerPoints;
    }

    public void setUserPowerPoints(Integer userPowerPoints) {
        this.userPowerPoints = userPowerPoints;
    }

    public Double getSuccessRate() {
        return successRate;
    }

    public void setSuccessRate(Double successRate) {
        this.successRate = successRate;
    }

    public Integer getMaxAttacks() {
        return maxAttacks;
    }

    public void setMaxAttacks(Integer maxAttacks) {
        this.maxAttacks = maxAttacks;
    }
}
