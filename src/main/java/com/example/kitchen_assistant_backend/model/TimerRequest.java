package com.example.kitchen_assistant_backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TimerRequest {
    private long duration;
    private String unit; // "seconds", "minutes", "hours"
    private String name;

    public TimerRequest(long duration,String unit,String name){
        this.duration = duration;
        this.name = name;
        this.unit = unit;
    }

    // --- Getters and Setters ---
    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}