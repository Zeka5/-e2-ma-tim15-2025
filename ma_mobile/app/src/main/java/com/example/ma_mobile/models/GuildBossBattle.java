package com.example.ma_mobile.models;

import com.google.gson.annotations.SerializedName;

public class GuildBossBattle {
    @SerializedName("id")
    private Long id;

    @SerializedName("guildId")
    private Long guildId;

    @SerializedName("guildName")
    private String guildName;

    @SerializedName("bossName")
    private String bossName;

    @SerializedName("maxHp")
    private Integer maxHp;

    @SerializedName("currentHp")
    private Integer currentHp;

    @SerializedName("memberCount")
    private Integer memberCount;

    @SerializedName("status")
    private String status;

    @SerializedName("startedAt")
    private String startedAt;

    @SerializedName("endsAt")
    private String endsAt;

    @SerializedName("completedAt")
    private String completedAt;

    @SerializedName("progressPercentage")
    private Double progressPercentage;

    public GuildBossBattle() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGuildId() {
        return guildId;
    }

    public void setGuildId(Long guildId) {
        this.guildId = guildId;
    }

    public String getGuildName() {
        return guildName;
    }

    public void setGuildName(String guildName) {
        this.guildName = guildName;
    }

    public String getBossName() {
        return bossName;
    }

    public void setBossName(String bossName) {
        this.bossName = bossName;
    }

    public Integer getMaxHp() {
        return maxHp;
    }

    public void setMaxHp(Integer maxHp) {
        this.maxHp = maxHp;
    }

    public Integer getCurrentHp() {
        return currentHp;
    }

    public void setCurrentHp(Integer currentHp) {
        this.currentHp = currentHp;
    }

    public Integer getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(Integer memberCount) {
        this.memberCount = memberCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(String startedAt) {
        this.startedAt = startedAt;
    }

    public String getEndsAt() {
        return endsAt;
    }

    public void setEndsAt(String endsAt) {
        this.endsAt = endsAt;
    }

    public String getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(String completedAt) {
        this.completedAt = completedAt;
    }

    public Double getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(Double progressPercentage) {
        this.progressPercentage = progressPercentage;
    }
}
