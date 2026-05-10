package com.ruraledu.service;

import com.ruraledu.entity.Enrollment;
import com.ruraledu.repository.EnrollmentRepository;
import com.ruraledu.service.EnrollmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EnrollmentServiceTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @InjectMocks
    private EnrollmentService enrollmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testIsEnrolled_True() {
        Long studentId = 1L;
        Long courseId = 2L;
        Enrollment enrollment = new Enrollment();

        when(enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId))
                .thenReturn(Optional.of(enrollment));

        boolean result = enrollmentService.isEnrolled(studentId, courseId);

        assertTrue(result);
        verify(enrollmentRepository, times(1)).findByStudentIdAndCourseId(studentId, courseId);
    }

    @Test
    void testIsEnrolled_False() {
        Long studentId = 1L;
        Long courseId = 2L;

        when(enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId))
                .thenReturn(Optional.empty());

        boolean result = enrollmentService.isEnrolled(studentId, courseId);

        assertFalse(result);
        verify(enrollmentRepository, times(1)).findByStudentIdAndCourseId(studentId, courseId);
    }
}
