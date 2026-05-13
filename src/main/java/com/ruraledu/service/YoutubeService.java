package com.ruraledu.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruraledu.dto.VideoMetadata;
import com.ruraledu.entity.Course;
import com.ruraledu.entity.Lesson;
import com.ruraledu.repository.CourseRepository;
import com.ruraledu.repository.LessonRepository;
import com.ruraledu.service.extractor.YoutubeExtractor;
import com.ruraledu.service.extractor.YoutubeExtractor.ValidationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class YoutubeService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Autowired
    private YoutubeExtractor youtubeExtractor;
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private LessonRepository lessonRepository;

    /**
     * Validates a YouTube URL and returns validation result.
     */
    public Map<String, Object> validateYoutubeUrl(String url) {
        ValidationResult result = youtubeExtractor.validateYoutubeUrl(url);
        
        Map<String, Object> response = new HashMap<>();
        response.put("valid", result.isValid());
        response.put("message", result.getMessage());
        response.put("id", result.getId());
        response.put("type", result.getType());
        
        return response;
    }

    /**
     * Imports a YouTube playlist as a course with lessons.
     */
    @Transactional
    public Map<String, Object> importPlaylistAsCourse(String title, String description, 
                                                       String playlistUrl, String category, 
                                                       String difficulty, Long teacherId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Validate URL first
            ValidationResult validation = youtubeExtractor.validateYoutubeUrl(playlistUrl);
            if (!validation.isValid()) {
                throw new IllegalArgumentException(validation.getMessage());
            }
            
            if (!"playlist".equals(validation.getType())) {
                throw new IllegalArgumentException("URL must be a YouTube playlist URL");
            }
            
            String playlistId = validation.getId();
            
            // Check for duplicate course
            Optional<Course> existingCourse = courseRepository.findByYoutubePlaylistId(playlistId);
            if (existingCourse.isPresent()) {
                throw new IllegalStateException("A course already exists for this playlist");
            }
            
            // Extract videos from playlist
            List<VideoMetadata> videos = youtubeExtractor.extractPlaylistVideos(playlistId);
            
            if (videos.isEmpty()) {
                throw new IllegalArgumentException("No videos found in playlist or playlist is unavailable");
            }
            
            // Create course
            Course course = new Course();
            course.setTitle(title);
            course.setDescription(description != null ? description : "");
            course.setCategory(category != null ? category : "General");
            course.setDifficulty(difficulty != null ? difficulty : "Beginner");
            course.setTeacherId(teacherId);
            course.setYoutubePlaylistId(playlistId);
            
            // Set thumbnail from first video
            if (!videos.isEmpty() && videos.get(0).getThumbnail() != null) {
                course.setThumbnail(videos.get(0).getThumbnail());
            } else {
                course.setThumbnail("https://i.ytimg.com/vi/" + videos.get(0).getVideoId() + "/mqdefault.jpg");
            }
            
            course = courseRepository.save(course);
            
            // Create lessons from videos
            List<Lesson> lessons = new ArrayList<>();
            for (VideoMetadata video : videos) {
                if (video.isAvailable()) {
                    Lesson lesson = new Lesson();
                    lesson.setCourse(course);
                    lesson.setTitle(video.getTitle());
                    lesson.setDescription(video.getDescription() != null ? video.getDescription() : "");
                    lesson.setVideoUrl("https://www.youtube.com/watch?v=" + video.getVideoId());
                    lesson.setThumbnail(video.getThumbnail());
                    lesson.setDuration(video.getDuration());
                    lesson.setOrderIndex(video.getOrderIndex());
                    lesson.setYoutubeVideoId(video.getVideoId());
                    lessons.add(lesson);
                }
            }
            
            lessonRepository.saveAll(lessons);
            
            response.put("success", true);
            response.put("message", "Course created successfully with " + lessons.size() + " lessons");
            response.put("courseId", course.getId());
            response.put("courseTitle", course.getTitle());
            response.put("lessonsCount", lessons.size());
            response.put("playlistId", playlistId);
            
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to import playlist: " + e.getMessage(), e);
        }
        
        return response;
    }

    /**
     * Syncs an existing course with its YouTube playlist.
     */
    @Transactional
    public Map<String, Object> syncCourseWithPlaylist(Long courseId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));
            
            if (course.getYoutubePlaylistId() == null || course.getYoutubePlaylistId().isEmpty()) {
                throw new IllegalArgumentException("Course is not linked to a YouTube playlist");
            }
            
            // Extract current videos from playlist
            List<VideoMetadata> videos = youtubeExtractor.extractPlaylistVideos(course.getYoutubePlaylistId());
            
            if (videos.isEmpty()) {
                throw new IllegalStateException("Playlist is empty or unavailable");
            }
            
            // Get existing lessons
            List<Lesson> existingLessons = lessonRepository.findByCourseId(courseId);
            
            int addedCount = 0;
            int updatedCount = 0;
            int removedCount = 0;
            
            // Update or add lessons
            Set<String> videoIdsInPlaylist = new HashSet<>();
            for (VideoMetadata video : videos) {
                videoIdsInPlaylist.add(video.getVideoId());
                
                Optional<Lesson> existingLesson = existingLessons.stream()
                    .filter(l -> video.getVideoId().equals(l.getYoutubeVideoId()))
                    .findFirst();
                
                if (existingLesson.isPresent()) {
                    // Update existing lesson
                    Lesson lesson = existingLesson.get();
                    lesson.setTitle(video.getTitle());
                    lesson.setDescription(video.getDescription() != null ? video.getDescription() : "");
                    lesson.setThumbnail(video.getThumbnail());
                    lesson.setDuration(video.getDuration());
                    lesson.setOrderIndex(video.getOrderIndex());
                    lessonRepository.save(lesson);
                    updatedCount++;
                } else {
                    // Add new lesson
                    if (video.isAvailable()) {
                        Lesson lesson = new Lesson();
                        lesson.setCourse(course);
                        lesson.setTitle(video.getTitle());
                        lesson.setDescription(video.getDescription() != null ? video.getDescription() : "");
                        lesson.setVideoUrl("https://www.youtube.com/watch?v=" + video.getVideoId());
                        lesson.setThumbnail(video.getThumbnail());
                        lesson.setDuration(video.getDuration());
                        lesson.setOrderIndex(video.getOrderIndex());
                        lesson.setYoutubeVideoId(video.getVideoId());
                        lessonRepository.save(lesson);
                        addedCount++;
                    }
                }
            }
            
            // Remove lessons that are no longer in playlist
            for (Lesson lesson : existingLessons) {
                if (lesson.getYoutubeVideoId() != null && !videoIdsInPlaylist.contains(lesson.getYoutubeVideoId())) {
                    lessonRepository.delete(lesson);
                    removedCount++;
                }
            }
            
            // Update course thumbnail if needed
            if (!videos.isEmpty() && videos.get(0).getThumbnail() != null) {
                course.setThumbnail(videos.get(0).getThumbnail());
                courseRepository.save(course);
            }
            
            response.put("success", true);
            response.put("message", "Course synced successfully");
            response.put("added", addedCount);
            response.put("updated", updatedCount);
            response.put("removed", removedCount);
            response.put("totalLessons", videos.size());
            
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to sync course: " + e.getMessage(), e);
        }
        
        return response;
    }

    /**
     * Fetches videos from a YouTube playlist using the extractor service.
     */
    public List<VideoMetadata> fetchPlaylistVideos(String playlistUrl) {
        String playlistId = extractPlaylistId(playlistUrl);
        if (playlistId == null) {
            return new ArrayList<>();
        }
        return youtubeExtractor.extractPlaylistVideos(playlistId);
    }

    /**
     * Extracts playlist ID from URL.
     */
    public String extractPlaylistId(String url) {
        if (url == null) return null;
        if (url.contains("list=")) {
            String id = url.split("list=")[1];
            int ampersandIndex = id.indexOf("&");
            if (ampersandIndex != -1) {
                return id.substring(0, ampersandIndex);
            }
            return id;
        }
        return null;
    }
}
