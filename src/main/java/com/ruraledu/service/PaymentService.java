package com.ruraledu.service;

import com.ruraledu.entity.Payment;
import com.ruraledu.entity.User;
import com.ruraledu.entity.Course;
import com.ruraledu.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private EnrollmentService enrollmentService;

    @Transactional
    public Payment processPayment(User user, Course course, Double amount) {
        // In a real scenario, this would call Stripe/Razorpay API
        // Here we simulate a successful transaction
        
        Payment payment = new Payment();
        payment.setUser(user);
        payment.setCourse(course);
        payment.setAmount(amount);
        payment.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        payment.setStatus("SUCCESS");
        payment.setPaymentDate(LocalDateTime.now());
        
        Payment savedPayment = paymentRepository.save(payment);
        
        // Auto-enroll user after successful payment
        enrollmentService.enroll(user, course);
        
        return savedPayment;
    }
}
