package com.ruraledu.dto;

import java.util.List;
import java.util.Map;

/**
 * DTO for YouTube video metadata
 */
public class VideoMetadata {
    private String videoId;
    private String title;
    private String description;
    private String thumbnail;
    private String duration;
    private Integer orderIndex;
    private boolean available;
    private boolean isPrivate;
    private boolean isDeleted;
    private boolean isAgeRestricted;
    private boolean isRegionBlocked;
    private String errorMessage;
    private String transcript;
    
    public VideoMetadata() {}
    
    public VideoMetadata(String videoId, String title, String thumbnail, Integer orderIndex) {
        this.videoId = videoId;
        this.title = title;
        this.thumbnail = thumbnail;
        this.orderIndex = orderIndex;
        this.available = true;
    }
    
    // Getters and Setters
    public String getVideoId() { return videoId; }
    public void setVideoId(String videoId) { this.videoId = videoId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getThumbnail() { return thumbnail; }
    public void setThumbnail(String thumbnail) { this.thumbnail = thumbnail; }
    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }
    public Integer getOrderIndex() { return orderIndex; }
    public void setOrderIndex(Integer orderIndex) { this.orderIndex = orderIndex; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    public boolean isPrivate() { return isPrivate; }
    public void setPrivate(boolean isPrivate) { this.isPrivate = isPrivate; }
    public boolean isDeleted() { return isDeleted; }
    public void setDeleted(boolean isDeleted) { this.isDeleted = isDeleted; }
    public boolean isAgeRestricted() { return isAgeRestricted; }
    public void setAgeRestricted(boolean isAgeRestricted) { this.isAgeRestricted = isAgeRestricted; }
    public boolean isRegionBlocked() { return isRegionBlocked; }
    public void setRegionBlocked(boolean isRegionBlocked) { this.isRegionBlocked = isRegionBlocked; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    public String getTranscript() { return transcript; }
    public void setTranscript(String transcript) { this.transcript = transcript; }
}
