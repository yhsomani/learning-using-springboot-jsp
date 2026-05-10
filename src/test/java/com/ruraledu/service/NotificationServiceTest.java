package com.ruraledu.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class NotificationServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private NotificationService notificationService;

    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalErr = System.err;
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        System.setErr(new PrintStream(errContent));
        System.setOut(new PrintStream(outContent));
    }

    @Test
    void testSendEmail_Success() {
        assertDoesNotThrow(() -> {
            notificationService.sendEmail("test@example.com", "Test Subject", "Test Body");
        });
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendEmail_ExceptionCaught() {
        doThrow(new MailSendException("SMTP error")).when(mailSender).send(any(SimpleMailMessage.class));

        assertDoesNotThrow(() -> {
            notificationService.sendEmail("test@example.com", "Test Subject", "Test Body");
        });

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
        assertTrue(errContent.toString().contains("Failed to send email to test@example.com: SMTP error"));
    }

    @Test
    void testSendEmail_MailSenderNull() {
        NotificationService serviceWithNullSender = new NotificationService();
        assertDoesNotThrow(() -> {
            serviceWithNullSender.sendEmail("test@example.com", "Test Subject", "Test Body");
        });
        assertTrue(outContent.toString().contains("MailSender not configured. Simulation: To: test@example.com | Sub: Test Subject"));
    }

    @Test
    void testSendWelcomeEmail() {
        assertDoesNotThrow(() -> {
            notificationService.sendWelcomeEmail("test@example.com", "John Doe");
        });
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendCourseCompletionEmail() {
        assertDoesNotThrow(() -> {
            notificationService.sendCourseCompletionEmail("test@example.com", "John Doe", "Java 101");
        });
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @org.junit.jupiter.api.AfterEach
    public void restoreStreams() {
        System.setErr(originalErr);
        System.setOut(originalOut);
    }
}
