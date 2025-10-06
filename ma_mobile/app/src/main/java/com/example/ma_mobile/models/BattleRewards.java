package com.example.ma_mobile.models;

import com.google.gson.annotations.SerializedName;

public class BattleRewards {
    @SerializedName("coinsEarned")
    private Integer coinsEarned;

    @SerializedName("equipmentType")
    private String equipmentType;

    @SerializedName("equipmentName")
    private String equipmentName;

    @SerializedName("equipmentId")
    private Long equipmentId;

    public BattleRewards() {
    }

    public Integer getCoinsEarned() {
        return coinsEarned;
    }

    public void setCoinsEarned(Integer coinsEarned) {
        this.coinsEarned = coinsEarned;
    }

    public String getEquipmentType() {
        return equipmentType;
    }

    public void setEquipmentType(String equipmentType) {
        this.equipmentType = equipmentType;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
    }

    public Long getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
    }
}
