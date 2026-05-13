package com.ruraledu.dto;

import java.util.List;
import java.util.Map;

/**
 * DTO for course import response with detailed results
 */
public class CourseImportResponse {
    private Long courseId;
    private String message;
    private int totalVideos;
    private int importedVideos;
    private int failedVideos;
    private boolean partialSuccess;
    private List<Map<String, Object>> importedLessons;
    private List<Map<String, Object>> failedLessons;
    
    public CourseImportResponse() {}
    
    // Getters and Setters
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public int getTotalVideos() { return totalVideos; }
    public void setTotalVideos(int totalVideos) { this.totalVideos = totalVideos; }
    public int getImportedVideos() { return importedVideos; }
    public void setImportedVideos(int importedVideos) { this.importedVideos = importedVideos; }
    public int getFailedVideos() { return failedVideos; }
    public void setFailedVideos(int failedVideos) { this.failedVideos = failedVideos; }
    public boolean isPartialSuccess() { return partialSuccess; }
    public void setPartialSuccess(boolean partialSuccess) { this.partialSuccess = partialSuccess; }
    public List<Map<String, Object>> getImportedLessons() { return importedLessons; }
    public void setImportedLessons(List<Map<String, Object>> importedLessons) { this.importedLessons = importedLessons; }
    public List<Map<String, Object>> getFailedLessons() { return failedLessons; }
    public void setFailedLessons(List<Map<String, Object>> failedLessons) { this.failedLessons = failedLessons; }
}
