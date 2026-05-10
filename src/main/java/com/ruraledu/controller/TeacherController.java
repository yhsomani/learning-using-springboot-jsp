package com.ruraledu.controller;

import com.ruraledu.entity.User;
import com.ruraledu.entity.Course;
import com.ruraledu.service.CourseService;
import com.ruraledu.service.UserService;
import com.ruraledu.repository.EnrollmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/teacher")
public class TeacherController {

    @Autowired
    private UserService userService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName()).orElseThrow();
        List<Course> courses = courseService.getCoursesByTeacher(user.getId());
        
        long totalStudents = courses.stream()
                .mapToLong(c -> enrollmentRepository.countByCourseId(c.getId()))
                .sum();
                
        double averageProgress = 0.0;
        if (totalStudents > 0) {
            averageProgress = courses.stream()
                .flatMap(c -> enrollmentRepository.findByCourseId(c.getId()).stream())
                .mapToInt(e -> e.getProgress())
                .average().orElse(0.0);
        }

        model.addAttribute("user", user);
        model.addAttribute("courses", courses);
        model.addAttribute("totalStudents", totalStudents);
        model.addAttribute("averageProgress", String.format("%.1f", averageProgress));
        return "teacher/dashboard";
    }
}
