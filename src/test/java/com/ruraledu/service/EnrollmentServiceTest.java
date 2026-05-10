package com.ruraledu.service;

import com.ruraledu.entity.Enrollment;
import com.ruraledu.repository.EnrollmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

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
    void testGetStudentEnrollments_Success() {
        Long studentId = 1L;
        Enrollment enrollment1 = new Enrollment();
        enrollment1.setId(10L);
        Enrollment enrollment2 = new Enrollment();
        enrollment2.setId(20L);

        List<Enrollment> expectedEnrollments = Arrays.asList(enrollment1, enrollment2);

        when(enrollmentRepository.findByStudentId(studentId)).thenReturn(expectedEnrollments);

        List<Enrollment> actualEnrollments = enrollmentService.getStudentEnrollments(studentId);

        assertEquals(2, actualEnrollments.size());
        assertEquals(10L, actualEnrollments.get(0).getId());
        assertEquals(20L, actualEnrollments.get(1).getId());
        verify(enrollmentRepository, times(1)).findByStudentId(studentId);
    }

    @Test
    void testGetStudentEnrollments_Empty() {
        Long studentId = 2L;

        when(enrollmentRepository.findByStudentId(studentId)).thenReturn(Collections.emptyList());

        List<Enrollment> actualEnrollments = enrollmentService.getStudentEnrollments(studentId);

        assertTrue(actualEnrollments.isEmpty());
        verify(enrollmentRepository, times(1)).findByStudentId(studentId);
    }
}
