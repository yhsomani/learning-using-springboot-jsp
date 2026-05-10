package com.ruraledu.service;

import com.ruraledu.entity.Course;
import com.ruraledu.entity.Enrollment;
import com.ruraledu.entity.User;
import com.ruraledu.repository.EnrollmentRepository;
import com.ruraledu.repository.LessonProgressRepository;
import com.ruraledu.repository.LessonRepository;
import com.ruraledu.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class EnrollmentServiceTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private CertificateService certificateService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private LessonProgressRepository lessonProgressRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private EnrollmentService enrollmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testEnroll_AlreadyEnrolled() {
        User student = new User();
        student.setId(1L);

        Course course = new Course();
        course.setId(10L);

        Enrollment existingEnrollment = new Enrollment();
        existingEnrollment.setId(100L);
        existingEnrollment.setStudent(student);
        existingEnrollment.setCourse(course);

        when(enrollmentRepository.findByStudentIdAndCourseId(1L, 10L)).thenReturn(Optional.of(existingEnrollment));

        Enrollment result = enrollmentService.enroll(student, course);

        assertNotNull(result);
        assertEquals(100L, result.getId());
        verify(enrollmentRepository, never()).save(any());
    }

    @Test
    void testEnroll_NewEnrollment() {
        User student = new User();
        student.setId(1L);

        Course course = new Course();
        course.setId(10L);

        Enrollment savedEnrollment = new Enrollment();
        savedEnrollment.setId(200L);
        savedEnrollment.setStudent(student);
        savedEnrollment.setCourse(course);
        savedEnrollment.setEnrollmentDate(LocalDateTime.now());
        savedEnrollment.setProgress(0);
        savedEnrollment.setCompleted(false);

        when(enrollmentRepository.findByStudentIdAndCourseId(1L, 10L)).thenReturn(Optional.empty());
        when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(savedEnrollment);

        Enrollment result = enrollmentService.enroll(student, course);

        assertNotNull(result);
        assertEquals(200L, result.getId());
        assertEquals(student, result.getStudent());
        assertEquals(course, result.getCourse());
        assertEquals(0, result.getProgress());
        assertFalse(result.isCompleted());
        assertNotNull(result.getEnrollmentDate());

        verify(enrollmentRepository, times(1)).save(any(Enrollment.class));
    }
}
