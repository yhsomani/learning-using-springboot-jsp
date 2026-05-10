package com.ruraledu.controller;

import com.ruraledu.entity.Course;
import com.ruraledu.entity.User;
import com.ruraledu.service.CourseService;
import com.ruraledu.service.EnrollmentService;
import com.ruraledu.service.UserService;
import com.ruraledu.service.ProjectSdgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectSdgService sdgService;

    @Autowired
    private com.ruraledu.repository.LessonProgressRepository progressRepository;

    @Autowired
    private com.ruraledu.repository.CertificateRepository certificateRepository;

    @Autowired
    private com.ruraledu.repository.UserRepository userRepository;

    @Autowired
    private com.ruraledu.repository.EnrollmentRepository enrollmentRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName()).orElseThrow();
        List<com.ruraledu.entity.Enrollment> enrollments = enrollmentService.getStudentEnrollments(user.getId());
        
        String preferredCategory = "IT"; // Default fallback
        if (!enrollments.isEmpty()) {
            preferredCategory = enrollments.get(0).getCourse().getCategory();
        }
        
        model.addAttribute("user", user);
        model.addAttribute("enrollments", enrollments);
        model.addAttribute("recommendations", courseService.getRecommendations(preferredCategory, user.getId()));
        model.addAttribute("newArrivals", courseService.getNewArrivals());
        model.addAttribute("certificates", certificateRepository.findByUserId(user.getId()));
        model.addAttribute("leaderboard", userRepository.findTop5ByOrderByPointsDesc());
        model.addAttribute("sdgImpact", sdgService.getSdg4ImpactMetrics());
        return "student/dashboard";
    }

    @GetMapping("/courses")
    public String courses(@RequestParam(required = false) String search, Model model) {
        if (search != null && !search.isEmpty()) {
            model.addAttribute("courses", courseService.searchCourses(search));
            model.addAttribute("searchKeyword", search);
        } else {
            model.addAttribute("courses", courseService.getAllCourses());
        }
        return "student/courses";
    }

    @GetMapping("/enroll/{courseId}")
    public String enroll(@PathVariable @org.springframework.lang.NonNull Long courseId, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName()).orElseThrow();
        Course course = courseService.getCourseById(courseId);
        enrollmentService.enroll(user, course);
        return "redirect:/student/dashboard";
    }

    @GetMapping("/course/{courseId}")
    public String viewCourse(@PathVariable @org.springframework.lang.NonNull Long courseId, Model model, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName()).orElseThrow();
        if (!enrollmentService.isEnrolled(user.getId(), courseId)) {
            return "redirect:/student/courses?error=notEnrolled";
        }
        Course course = courseService.getCourseById(courseId);
        List<com.ruraledu.entity.LessonProgress> progressList = progressRepository.findByStudentIdAndLessonCourseId(user.getId(), courseId);
        java.util.Set<Long> completedLessonIds = progressList.stream()
            .filter(com.ruraledu.entity.LessonProgress::isCompleted)
            .map(p -> p.getLesson().getId())
            .collect(java.util.stream.Collectors.toSet());

        model.addAttribute("course", course);
        model.addAttribute("user", user);
        model.addAttribute("completedLessonIds", completedLessonIds);
        model.addAttribute("enrollmentCount", enrollmentRepository.countByCourseId(courseId));
        return "student/course_view";
    }

    @GetMapping("/course/{courseId}/quiz")
    public String takeQuiz(@PathVariable @org.springframework.lang.NonNull Long courseId, Model model, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName()).orElseThrow();
        if (!enrollmentService.isEnrolled(user.getId(), courseId)) {
            return "redirect:/student/courses?error=notEnrolled";
        }
        Course course = courseService.getCourseById(courseId);
        if (course.getQuiz() == null) {
            return "redirect:/student/course/" + courseId + "?error=noQuiz";
        }
        model.addAttribute("course", course);
        model.addAttribute("quiz", course.getQuiz());
        model.addAttribute("user", user);
        return "student/quiz";
    }
}
