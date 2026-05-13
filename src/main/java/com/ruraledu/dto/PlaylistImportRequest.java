package com.ruraledu.dto;

import java.util.List;

/**
 * DTO for YouTube playlist import request
 */
public class PlaylistImportRequest {
    private String title;
    private String description;
    private String playlistUrl;
    private String category;
    private String difficulty;
    private boolean syncExisting;
    
    public PlaylistImportRequest() {}
    
    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getPlaylistUrl() { return playlistUrl; }
    public void setPlaylistUrl(String playlistUrl) { this.playlistUrl = playlistUrl; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public boolean isSyncExisting() { return syncExisting; }
    public void setSyncExisting(boolean syncExisting) { this.syncExisting = syncExisting; }
}
