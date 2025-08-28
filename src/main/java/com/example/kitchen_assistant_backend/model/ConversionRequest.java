package com.example.kitchen_assistant_backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class ConversionRequest {
    private String type;
    private double quantity;
    private String sourceUnit;
    private String targetUnit;


    public ConversionRequest(String type,double quantity,String sourceUnit,String targetUnit){
        this.quantity = quantity;
        this.sourceUnit = sourceUnit;
        this.type = type;
        this.targetUnit = targetUnit;
    }

    // --- Getters and Setters ---
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }

    public String getSourceUnit() { return sourceUnit; }
    public void setSourceUnit(String sourceUnit) { this.sourceUnit = sourceUnit; }

    public String getTargetUnit() { return targetUnit; }
    public void setTargetUnit(String targetUnit) { this.targetUnit = targetUnit; }
}