package com.ruraledu.controller;

import com.ruraledu.dto.PointsRequest;
import com.ruraledu.dto.QuizSubmissionRequest;
import com.ruraledu.entity.Course;
import com.ruraledu.entity.User;
import com.ruraledu.service.CourseService;
import com.ruraledu.service.EnrollmentService;
import com.ruraledu.service.UserService;
import com.ruraledu.service.CertificateService;
import com.ruraledu.repository.LessonRepository;
import com.ruraledu.repository.EnrollmentRepository;
import com.ruraledu.repository.CertificateRepository;
import com.ruraledu.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.ruraledu.exception.CourseNotFoundException;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private UserService userService;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private CertificateRepository certificateRepository;


    @GetMapping("/courses")
    public List<Course> getAllCourses() {
        return courseService.getAllCourses();
    }

    @GetMapping("/dashboard/stats")
    public ResponseEntity<?> getDashboardStats(Authentication authentication) {
        if (authentication == null) return ResponseEntity.status(401).body("Authentication required: Please log in to access this resource.");
        User user = userService.findByUsername(authentication.getName()).orElseThrow();
        
        Map<String, Object> data = new HashMap<>();
        data.put("points", user.getPoints());
        
        List<Map<String, Object>> activeCourses = enrollmentService.getStudentEnrollments(user.getId())
                .stream().map(e -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("courseId", e.getCourse().getId());
                    map.put("courseTitle", e.getCourse().getTitle());
                    map.put("category", e.getCourse().getCategory());
                    map.put("progress", e.getProgress());
                    map.put("thumbnailUrl", e.getCourse().getThumbnail());
                    return map;
                }).collect(Collectors.toList());
        data.put("activeCourses", activeCourses);
        
        return ResponseEntity.ok(data);
    }

    public ResponseEntity<?> updateProgress(@PathVariable @org.springframework.lang.NonNull Long lessonId, @RequestBody Map<String, Boolean> request, Authentication authentication) {
        if (authentication == null) return ResponseEntity.status(401).body("Authentication required: Please log in to access this resource.");
        User user = userService.findByUsername(authentication.getName()).orElseThrow();
        
        boolean completed = request.getOrDefault("completed", false);
        com.ruraledu.entity.Lesson lesson = lessonRepository.findById(lessonId).orElseThrow();
        
        enrollmentService.updateLessonProgress(user.getId(), lessonId, completed);
        
        // Auto-generate certificate if all lessons are done (simplification for MVP)
        // enrollmentService.checkAndCompleteEnrollment(user, lesson.getCourse());

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Lesson progress has been successfully synchronized.");
        response.put("lessonId", lessonId);
        response.put("courseTitle", lesson.getCourse().getTitle());
        response.put("studentId", user.getId());
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/lessons/{lessonId}/complete")
    public ResponseEntity<?> completeLesson(@PathVariable @org.springframework.lang.NonNull Long lessonId, Authentication authentication) {
        if (authentication == null) return ResponseEntity.status(401).body("Authentication required: Please log in to access this resource.");
        User user = userService.findByUsername(authentication.getName()).orElseThrow();
        
        enrollmentService.updateLessonProgress(user.getId(), lessonId, true);
        
        return ResponseEntity.ok(Map.of("message", "Well done! The lesson has been marked as complete and points have been awarded.", "pointsEarned", 10));
    }

    public ResponseEntity<?> submitQuiz(@PathVariable @org.springframework.lang.NonNull Long courseId, 
                                        @Valid @RequestBody QuizSubmissionRequest request, 
                                        Authentication authentication) {
        if (authentication == null) return ResponseEntity.status(401).body("Authentication required: Please log in to access this resource.");
        User user = userService.findByUsername(authentication.getName()).orElseThrow();
        
        int score = request.getScore();
        Course course = courseService.getCourseById(courseId);
        
        // Validate courseId matches path variable
        if (!course.getId().equals(courseId)) {
            return ResponseEntity.badRequest().body("Invalid request: The course identifier provided does not match the expected resource.");
        }
        
        Map<String, Object> response = new HashMap<>();
        if (score >= 80) {
            enrollmentService.completeCourse(user.getId(), courseId);
            response.put("passed", true);
            response.put("message", "Congratulations! You have successfully passed the final assessment.");
        } else {
            response.put("passed", false);
            response.put("message", "Assessment unsuccessful. Review the materials and please try again.");
        }
        
        response.put("score", score);
        response.put("courseTitle", course.getTitle());
        response.put("studentId", user.getId());
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/gamification/add-points")
    public ResponseEntity<?> addPoints(@Valid @RequestBody PointsRequest request, 
                                       Authentication authentication) {
        if (authentication == null) return ResponseEntity.status(401).body("Authentication required: Please log in to access this resource.");
        User user = userService.findByUsername(authentication.getName()).orElseThrow();

        int points = request.getPoints();
        
        // Additional business logic validation
        if (points > 50) {
            points = 50;
        } else if (points < 0) {
            points = 0;
        }

        user.setPoints(user.getPoints() + points);
        userService.updateUser(user);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Points have been successfully credited to your account.");
        response.put("totalPoints", user.getPoints());
        response.put("actionType", request.getActionType());
        response.put("entityId", request.getEntityId());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/certificates/fix")
    public ResponseEntity<?> fixCertificates(Authentication authentication) {
        if (authentication == null) return ResponseEntity.status(401).body("Authentication required: Please log in to access this resource.");
        User user = userService.findByUsername(authentication.getName()).orElseThrow();
        
        List<com.ruraledu.entity.Enrollment> completed = enrollmentRepository.findByStudentId(user.getId())
            .stream().filter(e -> e.isCompleted()).collect(Collectors.toList());
            
        for (com.ruraledu.entity.Enrollment e : completed) {
            certificateService.generateCertificate(user, e.getCourse());
        }
        
        return ResponseEntity.ok("Certificate regeneration has been successfully initiated for " + completed.size() + " courses.");
    }

    @GetMapping("/certificates")
    public ResponseEntity<?> getCertificates(Authentication authentication) {
        if (authentication == null) return ResponseEntity.status(401).body("Authentication required: Please log in to access this resource.");
        User user = userService.findByUsername(authentication.getName()).orElseThrow();
        List<com.ruraledu.entity.Certificate> certificates = certificateRepository.findByUserId(user.getId());
        
        List<Map<String, Object>> response = certificates.stream().map(c -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getId());
            map.put("courseId", c.getCourse().getId());
            map.put("courseTitle", c.getCourse().getTitle());
            map.put("issuedDate", c.getIssuedDate());
            return map;
        }).collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/settings/low-bandwidth")
    public ResponseEntity<?> toggleLowBandwidth(@RequestBody Map<String, Boolean> request, jakarta.servlet.http.HttpSession session) {
        boolean enabled = request.getOrDefault("enabled", false);
        session.setAttribute("lowBandwidthMode", enabled);
        String msg = enabled ? "Optimized low-bandwidth mode has been enabled for a smoother experience." : "Standard bandwidth mode has been restored.";
        return ResponseEntity.ok(Map.of("message", msg, "enabled", enabled));
    }

    @GetMapping("/certificates/{courseId}/download")
    public ResponseEntity<?> downloadCertificate(@PathVariable @org.springframework.lang.NonNull Long courseId, Authentication authentication) {
        if (authentication == null) return ResponseEntity.status(401).body("Authentication required: Please log in to access this resource.");
        User user = userService.findByUsername(authentication.getName()).orElseThrow();
        
        try {
            byte[] data = certificateService.getCertificateData(user.getId(), courseId);
            if (data == null) return ResponseEntity.notFound().build();
            
            return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"certificate.pdf\"")
                .contentType(org.springframework.http.MediaType.valueOf("application/pdf"))
                .body(data);
        } catch (CourseNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while retrieving the certificate document. Please contact support if the problem persists.");
        }
    }

    @Autowired
    private com.ruraledu.service.YoutubeService youtubeService;

    @PostMapping("/youtube/validate")
    public ResponseEntity<?> validateYoutubeUrl(@RequestBody Map<String, String> request) {
        String url = request.get("url");
        if (url == null || url.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                "valid", false,
                "message", "URL is required",
                "error_code", "URL_EMPTY"
            ));
        }

        try {
            Map<String, Object> result = youtubeService.validateYoutubeUrl(url);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                "valid", false,
                "message", e.getMessage(),
                "error_code", "VALIDATION_ERROR"
            ));
        }
    }

    @PostMapping("/youtube/import-playlist")
    public ResponseEntity<?> importPlaylist(@RequestBody Map<String, Object> request, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body(Map.of(
                "error", "Authentication required",
                "message", "Please log in to create courses"
            ));
        }

        try {
            User user = userService.findByUsername(authentication.getName()).orElseThrow();
            
            String title = (String) request.get("title");
            String description = (String) request.get("description");
            String playlistUrl = (String) request.get("playlistUrl");
            String category = (String) request.get("category");
            String difficulty = (String) request.get("difficulty");

            if (title == null || title.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "TITLE_REQUIRED",
                    "message", "Course title is required"
                ));
            }
            if (playlistUrl == null || playlistUrl.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "PLAYLIST_URL_REQUIRED",
                    "message", "YouTube playlist URL is required"
                ));
            }

            Map<String, Object> result = youtubeService.importPlaylistAsCourse(
                title.trim(),
                description != null ? description.trim() : "",
                playlistUrl.trim(),
                category != null ? category : "General",
                difficulty != null ? difficulty : "Beginner",
                user.getId()
            );

            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "VALIDATION_ERROR",
                "message", e.getMessage()
            ));
        } catch (IllegalStateException e) {
            return ResponseEntity.conflict().body(Map.of(
                "error", "CONFLICT_ERROR",
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "error", "IMPORT_ERROR",
                "message", "An unexpected error occurred while importing the playlist. Please try again."
            ));
        }
    }

    @PostMapping("/youtube/sync-course/{courseId}")
    public ResponseEntity<?> syncCourseWithPlaylist(@PathVariable Long courseId, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body(Map.of(
                "error", "Authentication required",
                "message", "Please log in to sync courses"
            ));
        }

        try {
            Map<String, Object> result = youtubeService.syncCourseWithPlaylist(courseId);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "VALIDATION_ERROR",
                "message", e.getMessage()
            ));
        } catch (IllegalStateException e) {
            return ResponseEntity.conflict().body(Map.of(
                "error", "SYNC_ERROR",
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "error", "SYNC_FAILED",
                "message", "Failed to sync course with playlist. Please try again."
            ));
        }
    }

    @GetMapping("/youtube/preview")
    public ResponseEntity<?> previewPlaylist(@RequestParam String url) {
        if (url == null || url.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "URL_REQUIRED",
                "message", "Playlist URL is required"
            ));
        }

        try {
            List<com.ruraledu.dto.VideoMetadata> videos = youtubeService.fetchPlaylistVideos(url.trim());
            return ResponseEntity.ok(Map.of(
                "success", true,
                "videoCount", videos.size(),
                "videos", videos
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", "INVALID_URL",
                "message", e.getMessage()
            ));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(404).body(Map.of(
                "success", false,
                "error", "PLAYLIST_UNAVAILABLE",
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "error", "EXTRACTION_FAILED",
                "message", "Failed to extract playlist videos. Please check the URL and try again."
            ));
        }
    }
}
