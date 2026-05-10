package com.ruraledu.service;

import com.ruraledu.exception.CourseNotFoundException;
import com.ruraledu.repository.CertificateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
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
    void testGetCertificateData_NotFound_ThrowsCourseNotFoundException() {
        Long studentId = 1L;
        Long courseId = 999L;

        when(certificateRepository.findByUserIdAndCourseId(studentId, courseId)).thenReturn(Optional.empty());

        assertThrows(CourseNotFoundException.class, () -> {
            certificateService.getCertificateData(studentId, courseId);
        });
    }
}
