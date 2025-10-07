package com.example.ma_mobile.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GuildBossMissionSummary {
    @SerializedName("battle")
    private GuildBossBattle battle;

    @SerializedName("memberProgress")
    private List<GuildBossMissionProgress> memberProgress;

    @SerializedName("totalDamageDealt")
    private Integer totalDamageDealt;

    @SerializedName("isActive")
    private Boolean isActive;

    public GuildBossMissionSummary() {
    }

    public GuildBossBattle getBattle() {
        return battle;
    }

    public void setBattle(GuildBossBattle battle) {
        this.battle = battle;
    }

    public List<GuildBossMissionProgress> getMemberProgress() {
        return memberProgress;
    }

    public void setMemberProgress(List<GuildBossMissionProgress> memberProgress) {
        this.memberProgress = memberProgress;
    }

    public Integer getTotalDamageDealt() {
        return totalDamageDealt;
    }

    public void setTotalDamageDealt(Integer totalDamageDealt) {
        this.totalDamageDealt = totalDamageDealt;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }
}
