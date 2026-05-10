package com.ruraledu.controller;

import com.ruraledu.entity.User;
import com.ruraledu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class MainController {

    @Autowired
    private UserService userService;

    @Autowired
    private com.ruraledu.repository.UserRepository userRepository;

    @Autowired
    private com.ruraledu.repository.CourseRepository courseRepository;

    @Autowired
    private com.ruraledu.repository.EnrollmentRepository enrollmentRepository;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("totalStudents", userRepository.count());
        model.addAttribute("totalCourses", courseRepository.count());
        model.addAttribute("avgProgress", enrollmentRepository.getAverageProgress() != null ? enrollmentRepository.getAverageProgress() : 0.0);
        return "index";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/api/login-info")
    @org.springframework.web.bind.annotation.ResponseBody
    public String login() {
        return "Backend Login Endpoint (Use POST /api/login for SPA)";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String handleRegister(@Valid @ModelAttribute("user") User user,
                                 BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("errors", result.getAllErrors().stream()
                .map(e -> e.getDefaultMessage())
                .collect(java.util.stream.Collectors.toList()));
            return "register";
        }
        try {
            userService.registerUser(user);
        } catch (com.ruraledu.exception.UserAlreadyExistsException e) {
            model.addAttribute("errors", java.util.Collections.singletonList(e.getMessage()));
            return "register";
        }
        return "redirect:/login";
    }

    @GetMapping("/main/dashboard")
    public String dashboard(Authentication authentication) {
        if (authentication == null) return "redirect:/login";
        
        return authentication.getAuthorities().stream()
                .map(auth -> auth.getAuthority())
                .filter(role -> role.startsWith("ROLE_"))
                .findFirst()
                .map(role -> {
                    switch (role) {
                        case "ROLE_STUDENT": return "redirect:/student/dashboard";
                        case "ROLE_TEACHER": return "redirect:/teacher/dashboard";
                        case "ROLE_PARENT": return "redirect:/parent/dashboard";
                        case "ROLE_ADMIN": return "redirect:/admin/dashboard";
                        default: return "redirect:/";
                    }
                })
                .orElse("redirect:/");
    }
}
