package com.ruraledu.service;

import com.ruraledu.entity.Course;
import com.ruraledu.entity.Payment;
import com.ruraledu.entity.User;
import com.ruraledu.entity.Enrollment;
import com.ruraledu.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private EnrollmentService enrollmentService;

    @InjectMocks
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testProcessPayment_Success() {
        User user = new User();
        user.setId(1L);
        Course course = new Course();
        course.setId(1L);
        Double amount = 100.0;

        Payment mockPayment = new Payment();
        mockPayment.setAmount(amount);
        mockPayment.setUser(user);
        mockPayment.setCourse(course);
        mockPayment.setStatus("SUCCESS");

        when(paymentRepository.save(any(Payment.class))).thenReturn(mockPayment);
        when(enrollmentService.enroll(any(), any())).thenReturn(new Enrollment());

        Payment result = paymentService.processPayment(user, course, amount);

        assertNotNull(result);
        assertEquals("SUCCESS", result.getStatus());
        assertEquals(amount, result.getAmount());

        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(enrollmentService, times(1)).enroll(user, course);
    }

    @Test
    void testProcessPayment_ZeroAmountThrowsException() {
        User user = new User();
        Course course = new Course();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            paymentService.processPayment(user, course, 0.0);
        });

        assertEquals("The payment amount provided must be greater than zero. Please check your entered amount and try again.", exception.getMessage());
        verify(paymentRepository, never()).save(any(Payment.class));
        verify(enrollmentService, never()).enroll(any(), any());
    }

    @Test
    void testProcessPayment_NegativeAmountThrowsException() {
        User user = new User();
        Course course = new Course();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            paymentService.processPayment(user, course, -10.0);
        });

        assertEquals("The payment amount provided must be greater than zero. Please check your entered amount and try again.", exception.getMessage());
        verify(paymentRepository, never()).save(any(Payment.class));
        verify(enrollmentService, never()).enroll(any(), any());
    }

    @Test
    void testProcessPayment_NullAmountThrowsException() {
        User user = new User();
        Course course = new Course();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            paymentService.processPayment(user, course, null);
        });

        assertEquals("The payment amount provided must be greater than zero. Please check your entered amount and try again.", exception.getMessage());
        verify(paymentRepository, never()).save(any(Payment.class));
        verify(enrollmentService, never()).enroll(any(), any());
    }
}
