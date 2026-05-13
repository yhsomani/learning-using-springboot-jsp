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

        if (playlistUrl == null || playlistUrl.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "YouTube Playlist URL is required to proceed with the import."));
        }

        String playlistId = youtubeService.extractPlaylistId(playlistUrl);
        if (playlistId == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "The provided YouTube Playlist URL is invalid. Please check the format and try again."));
        }

        // Idempotency check
        if (courseRepository.findByYoutubePlaylistUrl(playlistUrl).isPresent()) {
            return ResponseEntity.status(409).body(Map.of("message", "A course with this YouTube playlist has already been imported."));
        }

        User admin = userService.findByUsername(authentication.getName()).orElseThrow();

        Course course = new Course();
        course.setTitle(title);
        course.setDescription(description);
        course.setCategory(category);
        course.setDifficulty(difficulty);
        course.setYoutubePlaylistUrl(playlistUrl);
        course.setTeacher(admin);

        List<Map<String, Object>> videoItems = youtubeService.fetchPlaylistVideos(playlistId);
        if (videoItems.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Unable to retrieve videos from the playlist. Please verify that the playlist is public and contains valid videos."));
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

        if (lessons.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Import failed: The playlist must contain at least one public video."));
        }
        final Course savedCourse = courseService.saveCourseWithLessons(course, lessons);

        return ResponseEntity.ok(Map.of(
            "message", "The course and its associated lessons have been successfully imported from YouTube.",
            "courseId", savedCourse.getId(),
            "lessonCount", lessons.size()
        ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCourse(@PathVariable @org.springframework.lang.NonNull Long id, @RequestBody Course courseDetails) {
        Course course = courseService.getCourseById(id);
        if (course == null) return ResponseEntity.notFound().build();
        
        course.setTitle(courseDetails.getTitle());
        course.setDescription(courseDetails.getDescription());
        course.setCategory(courseDetails.getCategory());
        course.setDifficulty(courseDetails.getDifficulty());
        
        courseService.saveCourse(course);
        return ResponseEntity.ok(Map.of("message", "The course details have been successfully updated."));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCourse(@PathVariable @org.springframework.lang.NonNull Long id) {
        Course course = courseService.getCourseById(id);
        if (course == null) return ResponseEntity.notFound().build();
        
        // Custom delete logic in CourseService would be better
        courseService.deleteCourse(id);
        return ResponseEntity.ok(Map.of("message", "The course and all related content have been successfully deleted."));
    }
}
