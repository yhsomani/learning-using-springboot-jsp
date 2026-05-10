package com.ruraledu.service;

import com.ruraledu.entity.Certificate;
import com.ruraledu.exception.CourseNotFoundException;
import com.ruraledu.repository.CertificateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

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
    void testGetCertificateData_NotFound_ThrowsException() {
        when(certificateRepository.findByUserIdAndCourseId(anyLong(), anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(CourseNotFoundException.class, () -> {
            certificateService.getCertificateData(1L, 999L);
        });

        verify(certificateRepository, times(1)).findByUserIdAndCourseId(1L, 999L);
    }
}
