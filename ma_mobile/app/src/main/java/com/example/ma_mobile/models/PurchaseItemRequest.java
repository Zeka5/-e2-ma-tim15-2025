package com.example.ma_mobile.models;

import com.google.gson.annotations.SerializedName;

public class PurchaseItemRequest {
    @SerializedName("templateId")
    private Long templateId;

    @SerializedName("quantity")
    private Integer quantity;

    public PurchaseItemRequest(Long templateId, Integer quantity) {
        this.templateId = templateId;
        this.quantity = quantity;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
