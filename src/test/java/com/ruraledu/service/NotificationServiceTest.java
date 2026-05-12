package com.ruraledu.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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

    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private ByteArrayOutputStream outContent;
    private ByteArrayOutputStream errContent;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        outContent = new ByteArrayOutputStream();
        errContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    void testSendEmail_Success() {
        String to = "test@example.com";
        String subject = "Test Subject";
        String body = "Test Body";

        notificationService.sendEmail(to, subject, body);

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendEmail_MailSenderNull() {
        // Create a new instance with a null mailSender
        NotificationService nullMailSenderService = new NotificationService();

        String to = "test@example.com";
        String subject = "Test Subject";
        String body = "Test Body";

        nullMailSenderService.sendEmail(to, subject, body);

        // Verify output matches the expected print statement in the method when mailSender is null
        assertTrue(outContent.toString().contains("MailSender not configured. Simulation: To: " + to + " | Sub: " + subject));
    }

    @Test
    void testSendEmail_ExceptionCaught() {
        String to = "test@example.com";
        String subject = "Test Subject";
        String body = "Test Body";

        doThrow(new RuntimeException("Mail server down")).when(mailSender).send(any(SimpleMailMessage.class));

        // Exception should be caught within the service, not thrown out
        assertDoesNotThrow(() -> notificationService.sendEmail(to, subject, body));

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));

        // Verify output matches the expected print statement in the method when an exception happens
        assertTrue(errContent.toString().contains("Failed to send email to " + to + ": Mail server down"));
    }
}
