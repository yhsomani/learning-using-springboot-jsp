package com.ruraledu.service;

import com.ruraledu.entity.Course;
import com.ruraledu.entity.Enrollment;
import com.ruraledu.entity.Lesson;
import com.ruraledu.entity.LessonProgress;
import com.ruraledu.entity.User;
import com.ruraledu.repository.EnrollmentRepository;
import com.ruraledu.repository.LessonProgressRepository;
import com.ruraledu.repository.LessonRepository;
import com.ruraledu.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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
    void testEnroll_NewEnrollment() {
        User student = new User();
        student.setId(1L);

        Course course = new Course();
        course.setId(1L);

        when(enrollmentRepository.findByStudentIdAndCourseId(1L, 1L)).thenReturn(Optional.empty());
        when(enrollmentRepository.save(any(Enrollment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Enrollment result = enrollmentService.enroll(student, course);

        assertNotNull(result);
        assertEquals(student, result.getStudent());
        assertEquals(course, result.getCourse());
        assertEquals(0, result.getProgress());
        assertFalse(result.isCompleted());
        assertNotNull(result.getEnrollmentDate());

        verify(enrollmentRepository, times(1)).findByStudentIdAndCourseId(1L, 1L); // isEnrolled and inside if? Wait, isEnrolled calls findByStudentIdAndCourseId.
        verify(enrollmentRepository, times(1)).save(any(Enrollment.class));
    }

    @Test
    void testEnroll_AlreadyEnrolled() {
        User student = new User();
        student.setId(1L);

        Course course = new Course();
        course.setId(1L);

        Enrollment existingEnrollment = new Enrollment();
        existingEnrollment.setId(10L);
        existingEnrollment.setStudent(student);
        existingEnrollment.setCourse(course);

        when(enrollmentRepository.findByStudentIdAndCourseId(1L, 1L)).thenReturn(Optional.of(existingEnrollment));

        Enrollment result = enrollmentService.enroll(student, course);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        verify(enrollmentRepository, never()).save(any(Enrollment.class));
    }

    @Test
    void testUpdateProgress_Incomplete() {
        User student = new User();
        student.setId(1L);

        Course course = new Course();
        course.setId(1L);

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setProgress(50);
        enrollment.setCompleted(false);

        when(enrollmentRepository.findByStudentIdAndCourseId(1L, 1L)).thenReturn(Optional.of(enrollment));

        enrollmentService.updateProgress(1L, 1L, 80);

        assertEquals(80, enrollment.getProgress());
        assertFalse(enrollment.isCompleted());
        verify(enrollmentRepository, times(1)).save(enrollment);
        verify(certificateService, never()).generateCertificate(any(), any());
        verify(notificationService, never()).sendCourseCompletionEmail(any(), any(), any());
    }

    @Test
    void testUpdateProgress_Complete() {
        User student = new User();
        student.setId(1L);
        student.setEmail("student@example.com");
        student.setFullName("John Doe");

        Course course = new Course();
        course.setId(1L);
        course.setTitle("Java Basics");

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setProgress(80);
        enrollment.setCompleted(false);

        when(enrollmentRepository.findByStudentIdAndCourseId(1L, 1L)).thenReturn(Optional.of(enrollment));

        enrollmentService.updateProgress(1L, 1L, 100);

        assertEquals(100, enrollment.getProgress());
        assertTrue(enrollment.isCompleted());
        assertNotNull(enrollment.getCompletionDate());

        verify(enrollmentRepository, times(1)).save(enrollment);
        verify(certificateService, times(1)).generateCertificate(student, course);
        verify(notificationService, times(1)).sendCourseCompletionEmail("student@example.com", "John Doe", "Java Basics");
    }

    @Test
    void testUpdateLessonProgress_FirstTimeComplete() {
        User student = new User();
        student.setId(1L);
        student.setPoints(100);

        Course course = new Course();
        course.setId(1L);

        Lesson lesson = new Lesson();
        lesson.setId(10L);
        lesson.setCourse(course);

        LessonProgress progress = new LessonProgress();
        // progress.getId() == null means it's newly created if we use standard approach, but findBy returns Optional.empty

        when(lessonRepository.findById(10L)).thenReturn(Optional.of(lesson));
        when(lessonProgressRepository.findByStudentIdAndLessonId(1L, 10L)).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(student));

        Lesson lesson1 = new Lesson(); lesson1.setId(10L); lesson1.setCourse(course);
        Lesson lesson2 = new Lesson(); lesson2.setId(11L); lesson2.setCourse(course);
        when(lessonRepository.findByCourseId(1L)).thenReturn(Arrays.asList(lesson1, lesson2));

        LessonProgress savedProgress = new LessonProgress();
        savedProgress.setCompleted(true);
        when(lessonProgressRepository.findByStudentIdAndLessonCourseId(1L, 1L)).thenReturn(Collections.singletonList(savedProgress));

        Enrollment enrollment = new Enrollment();
        when(enrollmentRepository.findByStudentIdAndCourseId(1L, 1L)).thenReturn(Optional.of(enrollment));

        enrollmentService.updateLessonProgress(1L, 10L, true);

        // Verify points added
        assertEquals(110, student.getPoints());
        verify(userRepository, times(1)).save(student);

        // Verify progress save
        ArgumentCaptor<LessonProgress> progressCaptor = ArgumentCaptor.forClass(LessonProgress.class);
        verify(lessonProgressRepository, times(1)).save(progressCaptor.capture());
        assertTrue(progressCaptor.getValue().isCompleted());
        assertEquals(student, progressCaptor.getValue().getStudent());
        assertEquals(lesson, progressCaptor.getValue().getLesson());

        // Verify overall progress update
        assertEquals(50, enrollment.getProgress()); // 1 out of 2 lessons completed = 50%
    }
}
