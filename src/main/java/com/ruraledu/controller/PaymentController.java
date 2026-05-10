package com.ruraledu.controller;

import com.ruraledu.entity.Course;
import com.ruraledu.entity.User;
import com.ruraledu.service.CourseService;
import com.ruraledu.service.PaymentService;
import com.ruraledu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserService userService;

    @GetMapping("/checkout/{courseId}")
    public String checkout(@PathVariable Long courseId, Model model, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName()).orElseThrow();
        Course course = courseService.getCourseById(courseId);
        
        model.addAttribute("user", user);
        model.addAttribute("course", course);
        return "student/checkout";
    }

    @PostMapping("/process")
    public String process(@RequestParam Long courseId, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName()).orElseThrow();
        Course course = courseService.getCourseById(courseId);
        
        // Use a fixed price or dynamic price if added to course
        Double amount = 499.0; 
        
        paymentService.processPayment(user, course, amount);
        
        return "redirect:/student/dashboard?payment=success";
    }
}
