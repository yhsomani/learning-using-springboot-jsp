package com.ruraledu.service;

import com.ruraledu.entity.Certificate;
import com.ruraledu.entity.Course;
import com.ruraledu.entity.User;
import com.ruraledu.repository.CertificateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class CertificateServicePerformanceTest {

    @Mock
    private CertificateRepository certificateRepository;

    @InjectMocks
    private CertificateService certificateService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGenerateCertificate_RedundancyCheck() {
        User student = new User();
        student.setId(1L);
        student.setUsername("teststudent");

        Course course = new Course();
        course.setId(1L);
        course.setTitle("Test Course");

        when(certificateRepository.findByUserIdAndCourseId(1L, 1L))
            .thenReturn(Optional.of(new Certificate()));

        certificateService.generateCertificate(student, course);

        // Verify that no further interactions happened with certificateRepository after the find check
        // because it should have skipped the generation.
        verify(certificateRepository, times(1)).findByUserIdAndCourseId(1L, 1L);
        verifyNoMoreInteractions(certificateRepository);
    }
}
