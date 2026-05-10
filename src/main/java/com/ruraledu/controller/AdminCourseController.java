package com.ruraledu.controller;

import com.ruraledu.entity.Course;
import com.ruraledu.entity.Lesson;
import com.ruraledu.entity.User;
import com.ruraledu.repository.CourseRepository;
import com.ruraledu.service.YoutubeService;
import com.ruraledu.service.UserService;
import com.ruraledu.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/courses")
@PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
public class AdminCourseController {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseService courseService;

    @Autowired
    private YoutubeService youtubeService;

    @Autowired
    private UserService userService;

    @PostMapping("/import")
    @SuppressWarnings("null")
    public ResponseEntity<?> importPlaylist(@RequestBody Map<String, String> request, Authentication authentication) {
        String playlistUrl = request.get("playlistUrl");
        String title = request.get("title");
        String description = request.get("description");
        String category = request.get("category");
        String difficulty = request.get("difficulty");
        String customApiKey = request.get("apiKey");

        if (playlistUrl == null || playlistUrl.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Playlist URL is required"));
        }

        String playlistId = youtubeService.extractPlaylistId(playlistUrl);
        if (playlistId == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid YouTube Playlist URL"));
        }

        // Idempotency check
        if (courseRepository.findByYoutubePlaylistUrl(playlistUrl).isPresent()) {
            return ResponseEntity.status(409).body(Map.of("message", "Course already exists for this playlist"));
        }

        User admin = userService.findByUsername(authentication.getName()).orElseThrow();

        Course course = new Course();
        course.setTitle(title);
        course.setDescription(description);
        course.setCategory(category);
        course.setDifficulty(difficulty);
        course.setYoutubePlaylistUrl(playlistUrl);
        course.setTeacher(admin);

        List<Map<String, Object>> videoItems = youtubeService.fetchPlaylistVideos(playlistId, customApiKey);
        if (videoItems.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Could not fetch videos from playlist. Check your API key and playlist visibility."));
        }

        // Use first video thumbnail as course thumbnail
        course.setThumbnail((String) videoItems.get(0).get("thumbnail"));
        
        List<Lesson> lessons = videoItems.stream().map(item -> {
            return new Lesson(
                    null, // Course will be set by service
                    (String) item.get("videoId"),
                    (String) item.get("title"),
                    (String) item.get("thumbnail"),
                    (Integer) item.get("orderIndex")
            );
        }).collect(Collectors.toList());

        final Course savedCourse = courseService.saveCourseWithLessons(course, lessons);

        return ResponseEntity.ok(Map.of(
            "message", "Course imported successfully",
            "courseId", savedCourse.getId(),
            "lessonCount", lessons.size()
        ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCourse(@PathVariable Long id, @RequestBody Course courseDetails) {
        Course course = courseService.getCourseById(id);
        if (course == null) return ResponseEntity.notFound().build();
        
        course.setTitle(courseDetails.getTitle());
        course.setDescription(courseDetails.getDescription());
        course.setCategory(courseDetails.getCategory());
        course.setDifficulty(courseDetails.getDifficulty());
        
        courseService.saveCourse(course);
        return ResponseEntity.ok(Map.of("message", "Course updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCourse(@PathVariable Long id) {
        Course course = courseService.getCourseById(id);
        if (course == null) return ResponseEntity.notFound().build();
        
        // Custom delete logic in CourseService would be better
        courseService.deleteCourse(id);
        return ResponseEntity.ok(Map.of("message", "Course deleted successfully"));
    }
}
