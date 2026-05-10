package com.ruraledu.service;

import com.ruraledu.entity.Certificate;
import com.ruraledu.repository.CertificateRepository;
import com.ruraledu.exception.CourseNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

public class CertificateServiceTest {

    @Mock
    private CertificateRepository certificateRepository;

    @InjectMocks
    private CertificateService certificateService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetCertificateData_InvalidCourseId() {
        Long studentId = 1L;
        Long invalidCourseId = 999L;

        // Mock repository to return empty
        when(certificateRepository.findByUserIdAndCourseId(studentId, invalidCourseId))
                .thenReturn(Optional.empty());

        assertThrows(CourseNotFoundException.class, () -> {
            certificateService.getCertificateData(studentId, invalidCourseId);
        }, "Expected CourseNotFoundException when certificate is not found for the given course ID");
    }
}
