package com.ruraledu.controller;

import com.ruraledu.entity.User;
import com.ruraledu.entity.Enrollment;
import com.ruraledu.service.UserService;
import com.ruraledu.repository.EnrollmentRepository;
import com.ruraledu.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/parent")
@PreAuthorize("hasRole('PARENT')")
public class ParentController {

    @Autowired
    private UserService userService;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        User parent = userService.findByUsername(authentication.getName()).orElseThrow();
        List<User> children = userService.getChildren(parent.getId());
        
        List<Map<String, Object>> childStats = children.stream().map(child -> {
            Map<String, Object> stats = new HashMap<>();
            stats.put("user", child);
            List<Enrollment> enrollments = enrollmentRepository.findByStudentId(child.getId());
            stats.put("enrollments", enrollments);
            stats.put("courseCount", enrollments.size());
            stats.put("avgProgress", enrollments.stream().mapToInt(Enrollment::getProgress).average().orElse(0.0));
            return stats;
        }).collect(Collectors.toList());

        model.addAttribute("parent", parent);
        model.addAttribute("childStats", childStats);
        return "parent/dashboard";
    }

    @PostMapping("/add-child")
    public String addChild(@RequestParam String childUsername, Authentication authentication, RedirectAttributes redirectAttributes) {
        User parent = userService.findByUsername(authentication.getName()).orElseThrow();
        User child = userService.findByUsername(childUsername).orElse(null);

        if (child == null) {
            redirectAttributes.addFlashAttribute("error", "Student not found with username: " + childUsername);
            return "redirect:/parent/dashboard";
        }

        if (child.getRole() != User.Role.STUDENT) {
            redirectAttributes.addFlashAttribute("error", "The specified user is not a student.");
            return "redirect:/parent/dashboard";
        }

        if (child.getParent() != null) {
            redirectAttributes.addFlashAttribute("error", "This student is already linked to another parent.");
            return "redirect:/parent/dashboard";
        }

        child.setParent(parent);
        userRepository.save(child);
        
        redirectAttributes.addFlashAttribute("success", "Successfully linked " + child.getFullName() + " to your account.");
        return "redirect:/parent/dashboard";
    }

    @PostMapping("/remove-child")
    public String removeChild(@RequestParam Long childId, Authentication authentication, RedirectAttributes redirectAttributes) {
        User parent = userService.findByUsername(authentication.getName()).orElseThrow();
        User child = userRepository.findById(childId).orElse(null);

        if (child != null && child.getParent() != null && child.getParent().getId().equals(parent.getId())) {
            child.setParent(null);
            userRepository.save(child);
            redirectAttributes.addFlashAttribute("success", "Successfully unlinked " + child.getFullName() + ".");
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to unlink student.");
        }
        
        return "redirect:/parent/dashboard";
    }
}
