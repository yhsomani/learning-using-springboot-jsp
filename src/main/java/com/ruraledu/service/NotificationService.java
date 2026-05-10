package com.ruraledu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Async
    public void sendEmail(String to, String subject, String body) {
        if (mailSender == null) {
            System.out.println("MailSender not configured. Simulation: To: " + to + " | Sub: " + subject);
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("no-reply@ruraledu.com");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send email to " + to + ": " + e.getMessage());
        }
    }

    public void sendWelcomeEmail(String to, String fullName) {
        String subject = "Welcome to RuralEduHub!";
        String body = "Hello " + fullName + ",\n\nWelcome to our platform. We are excited to help you on your learning journey.\n\nBest Regards,\nRuralEduHub Team";
        sendEmail(to, subject, body);
    }

    public void sendCourseCompletionEmail(String to, String fullName, String courseTitle) {
        String subject = "Congratulations! Course Completed";
        String body = "Hello " + fullName + ",\n\nYou have successfully completed the course: " + courseTitle + ".\nYour certificate is now available in your dashboard.\n\nBest Regards,\nRuralEduHub Team";
        sendEmail(to, subject, body);
    }
}
