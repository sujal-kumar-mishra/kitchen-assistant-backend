package com.example.kitchen_assistant_backend.model;

public class ActiveTimerInfo {
    public String id;
    public String name;
    public long remainingSeconds;
    public ActiveTimerInfo(String id, String name, long remainingSeconds) {
        this.id = id; this.name = name; this.remainingSeconds = remainingSeconds;
    }
}
