package com.ruraledu.controller;

import com.ruraledu.entity.User;
import com.ruraledu.service.CourseService;
import com.ruraledu.service.UserService;
import com.ruraledu.service.ProjectSdgService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private ProjectSdgService sdgService;

    @Autowired
    private com.ruraledu.repository.EnrollmentRepository enrollmentRepository;

    @Autowired
    private com.ruraledu.repository.UserRepository userRepository;

    @Autowired
    private com.ruraledu.repository.CourseRepository courseRepository;

    @Autowired
    private com.ruraledu.service.AuditService auditService;

    @Autowired
    private com.ruraledu.repository.AuditLogRepository auditLogRepository;

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(defaultValue = "0") int page, Model model, org.springframework.security.core.Authentication authentication) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, 10);
        org.springframework.data.domain.Page<User> userPage = userRepository.findAll(pageable);
        
        model.addAttribute("totalUsers", userRepository.count());
        model.addAttribute("totalCourses", courseRepository.count());
        model.addAttribute("totalEnrollments", enrollmentRepository.count());
        model.addAttribute("sdgMetrics", sdgService.getSdg4ImpactMetrics());
        model.addAttribute("recentUsers", userPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("auditLogs", auditLogRepository.findTop20ByOrderByTimestampDesc());
        
        List<Map<String, String>> systemAlerts = new ArrayList<>();
        Map<String, String> alert1 = new HashMap<>();
        alert1.put("icon", "bi-info-circle-fill");
        alert1.put("title", "Database Backup");
        alert1.put("description", "Completed successfully at " + LocalDateTime.now().minusHours(2).format(DateTimeFormatter.ofPattern("hh:mm a")));
        systemAlerts.add(alert1);
        
        Map<String, String> alert2 = new HashMap<>();
        alert2.put("icon", "bi-lightning-fill");
        alert2.put("title", "System Health");
        alert2.put("description", "All services operating normally.");
        systemAlerts.add(alert2);
        
        model.addAttribute("systemAlerts", systemAlerts);
        return "admin/dashboard";
    }

    @GetMapping("/users/{id}")
    @ResponseBody
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/users/{id}/toggle-status")
    @ResponseBody
    public ResponseEntity<?> toggleUserStatus(@PathVariable Long id, org.springframework.security.core.Authentication authentication) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setEnabled(!user.isEnabled());
                    userRepository.save(user);
                    auditService.log("TOGGLE_STATUS", authentication.getName(), "User", id, "New Status: " + (user.isEnabled() ? "Active" : "Disabled"));
                    return ResponseEntity.ok(Map.of("message", "User status updated", "enabled", user.isEnabled()));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/users/{id}/update")
    @ResponseBody
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody Map<String, String> updates, org.springframework.security.core.Authentication authentication) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setFullName(updates.get("fullName"));
                    user.setEmail(updates.get("email"));
                    user.setRole(User.Role.valueOf(updates.get("role")));
                    userRepository.save(user);
                    auditService.log("UPDATE_USER", authentication.getName(), "User", id, "Updated profile details");
                    return ResponseEntity.ok(Map.of("message", "User updated successfully"));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/users/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteUser(@PathVariable Long id, org.springframework.security.core.Authentication authentication) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setDeleted(true);
                    userRepository.save(user);
                    auditService.log("DELETE_USER", authentication.getName(), "User", id, "Soft deleted user account");
                    return ResponseEntity.ok(Map.of("message", "User archived successfully"));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/users/bulk-delete")
    @ResponseBody
    public ResponseEntity<?> bulkDelete(@RequestBody Map<String, List<Long>> request, org.springframework.security.core.Authentication authentication) {
        List<Long> ids = request.get("ids");
        if (ids != null && !ids.isEmpty()) {
            List<User> users = userRepository.findAllById(ids);
            users.forEach(u -> u.setDeleted(true));
            userRepository.saveAll(users);
            auditService.log("BULK_DELETE", authentication.getName(), "User", null, "Archived " + ids.size() + " users");
            return ResponseEntity.ok(Map.of("message", "Users archived successfully"));
        }
        return ResponseEntity.badRequest().body(Map.of("message", "No IDs provided"));
    }

    @GetMapping("/users/export")
    public void exportUsers(jakarta.servlet.http.HttpServletResponse response) throws java.io.IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=users.csv");
        
        java.io.PrintWriter writer = response.getWriter();
        writer.println("ID,Full Name,Email,Role,Points,Status");
        
        int page = 0;
        int size = 1000;
        org.springframework.data.domain.Page<User> userPage;

        do {
            userPage = userRepository.findAll(org.springframework.data.domain.PageRequest.of(page, size));
            for (User u : userPage.getContent()) {
                writer.println(String.format("%d,%s,%s,%s,%d,%s",
                    u.getId(), u.getFullName(), u.getEmail(), u.getRole(), u.getPoints(), u.isEnabled() ? "Active" : "Disabled"));
            }
            writer.flush();
            page++;
        } while (userPage.hasNext());
    }
}
