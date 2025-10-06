package com.example.ma_mobile.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AttackRequest {
    @SerializedName("battleId")
    private Long battleId;

    @SerializedName("activeEquipmentIds")
    private List<Long> activeEquipmentIds;

    public AttackRequest() {
    }

    public AttackRequest(Long battleId, List<Long> activeEquipmentIds) {
        this.battleId = battleId;
        this.activeEquipmentIds = activeEquipmentIds;
    }

    public Long getBattleId() {
        return battleId;
    }

    public void setBattleId(Long battleId) {
        this.battleId = battleId;
    }

    public List<Long> getActiveEquipmentIds() {
        return activeEquipmentIds;
    }

    public void setActiveEquipmentIds(List<Long> activeEquipmentIds) {
        this.activeEquipmentIds = activeEquipmentIds;
    }
}
