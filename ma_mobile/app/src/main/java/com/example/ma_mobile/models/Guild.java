package com.example.ma_mobile.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class Guild implements Serializable {
    @SerializedName("id")
    private Long id;

    @SerializedName("name")
    private String name;

    @SerializedName("leader")
    private User leader;

    @SerializedName("members")
    private List<User> members;

    @SerializedName("hasActiveMission")
    private Boolean hasActiveMission;

    @SerializedName("createdAt")
    private String createdAt;

    public Guild() {
    }

    public Guild(Long id, String name, User leader, List<User> members, Boolean hasActiveMission, String createdAt) {
        this.id = id;
        this.name = name;
        this.leader = leader;
        this.members = members;
        this.hasActiveMission = hasActiveMission;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getLeader() {
        return leader;
    }

    public void setLeader(User leader) {
        this.leader = leader;
    }

    public List<User> getMembers() {
        return members;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }

    public Boolean getHasActiveMission() {
        return hasActiveMission;
    }

    public void setHasActiveMission(Boolean hasActiveMission) {
        this.hasActiveMission = hasActiveMission;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getMemberCount() {
        return members != null ? members.size() : 0;
    }

    public boolean isLeader(Long userId) {
        return leader != null && leader.getId() != null && leader.getId().equals(userId);
    }
}
