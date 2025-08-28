package com.example.kitchen_assistant_backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VideoResult {
    private String videoId;
    private String title;
    private String thumbnailUrl;

    // --- THIS IS THE MISSING CONSTRUCTOR ---
    public VideoResult(String videoId, String title, String thumbnailUrl) {
        this.videoId = videoId;
        this.title = title;
        this.thumbnailUrl = thumbnailUrl;
    }

    // --- Getters and Setters ---
    public String getVideoId() { return videoId; }
    public void setVideoId(String videoId) { this.videoId = videoId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
}