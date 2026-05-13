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

    @Autowired
    private UserRepository userRepository;

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

    @PostMapping("/lessons/{lessonId}/progress")
    public ResponseEntity<?> updateProgress(@PathVariable Long lessonId, @RequestBody Map<String, Boolean> request, Authentication authentication) {
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
    public ResponseEntity<?> completeLesson(@PathVariable Long lessonId, Authentication authentication) {
        if (authentication == null) return ResponseEntity.status(401).body("Authentication required: Please log in to access this resource.");
        User user = userService.findByUsername(authentication.getName()).orElseThrow();
        
        enrollmentService.updateLessonProgress(user.getId(), lessonId, true);
        
        return ResponseEntity.ok(Map.of("message", "Well done! The lesson has been marked as complete and points have been awarded.", "pointsEarned", 10));
    }

    @PostMapping("/courses/{courseId}/quiz/submit")
    public ResponseEntity<?> submitQuiz(@PathVariable Long courseId, 
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
    public ResponseEntity<?> downloadCertificate(@PathVariable Long courseId, Authentication authentication) {
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
}
