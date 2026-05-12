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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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

    private User testStudent;
    private Course testCourse;
    private Enrollment testEnrollment;
    private Lesson testLesson;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testStudent = new User();
        testStudent.setId(1L);
        testStudent.setEmail("student@example.com");
        testStudent.setFullName("Test Student");
        testStudent.setPoints(0);

        testCourse = new Course();
        testCourse.setId(1L);
        testCourse.setTitle("Test Course");

        testEnrollment = new Enrollment();
        testEnrollment.setId(1L);
        testEnrollment.setStudent(testStudent);
        testEnrollment.setCourse(testCourse);
        testEnrollment.setProgress(0);
        testEnrollment.setCompleted(false);

        testLesson = new Lesson();
        testLesson.setId(1L);
        testLesson.setCourse(testCourse);
    }

    @Test
    void testEnroll_NewEnrollment() {
        when(enrollmentRepository.findByStudentIdAndCourseId(testStudent.getId(), testCourse.getId()))
                .thenReturn(Optional.empty());
        when(enrollmentRepository.save(any(Enrollment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Enrollment result = enrollmentService.enroll(testStudent, testCourse);

        assertNotNull(result);
        assertEquals(testStudent, result.getStudent());
        assertEquals(testCourse, result.getCourse());
        assertEquals(0, result.getProgress());
        assertFalse(result.isCompleted());
        assertNotNull(result.getEnrollmentDate());

        verify(enrollmentRepository).findByStudentIdAndCourseId(testStudent.getId(), testCourse.getId());
        verify(enrollmentRepository).save(any(Enrollment.class));
    }

    @Test
    void testEnroll_ExistingEnrollment() {
        when(enrollmentRepository.findByStudentIdAndCourseId(testStudent.getId(), testCourse.getId()))
                .thenReturn(Optional.of(testEnrollment));

        Enrollment result = enrollmentService.enroll(testStudent, testCourse);

        assertNotNull(result);
        assertEquals(testEnrollment, result);

        verify(enrollmentRepository, times(2)).findByStudentIdAndCourseId(testStudent.getId(), testCourse.getId());
        verify(enrollmentRepository, never()).save(any(Enrollment.class));
    }

    @Test
    void testIsEnrolled() {
        when(enrollmentRepository.findByStudentIdAndCourseId(testStudent.getId(), testCourse.getId()))
                .thenReturn(Optional.of(testEnrollment));

        boolean result = enrollmentService.isEnrolled(testStudent.getId(), testCourse.getId());

        assertTrue(result);
        verify(enrollmentRepository).findByStudentIdAndCourseId(testStudent.getId(), testCourse.getId());
    }

    @Test
    void testGetStudentEnrollments() {
        List<Enrollment> enrollments = Collections.singletonList(testEnrollment);
        when(enrollmentRepository.findByStudentId(testStudent.getId())).thenReturn(enrollments);

        List<Enrollment> result = enrollmentService.getStudentEnrollments(testStudent.getId());

        assertEquals(1, result.size());
        assertEquals(testEnrollment, result.get(0));
        verify(enrollmentRepository).findByStudentId(testStudent.getId());
    }

    @Test
    void testUpdateProgress_NotCompleted() {
        when(enrollmentRepository.findByStudentIdAndCourseId(testStudent.getId(), testCourse.getId()))
                .thenReturn(Optional.of(testEnrollment));
        when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(testEnrollment);

        enrollmentService.updateProgress(testStudent.getId(), testCourse.getId(), 50);

        assertEquals(50, testEnrollment.getProgress());
        assertFalse(testEnrollment.isCompleted());
        verify(enrollmentRepository).save(testEnrollment);
        verify(certificateService, never()).generateCertificate(any(), any());
        verify(notificationService, never()).sendCourseCompletionEmail(any(), any(), any());
    }

    @Test
    void testUpdateProgress_Completed() {
        when(enrollmentRepository.findByStudentIdAndCourseId(testStudent.getId(), testCourse.getId()))
                .thenReturn(Optional.of(testEnrollment));
        when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(testEnrollment);

        enrollmentService.updateProgress(testStudent.getId(), testCourse.getId(), 100);

        assertEquals(100, testEnrollment.getProgress());
        assertTrue(testEnrollment.isCompleted());
        assertNotNull(testEnrollment.getCompletionDate());
        verify(enrollmentRepository).save(testEnrollment);
        verify(certificateService).generateCertificate(testStudent, testCourse);
        verify(notificationService).sendCourseCompletionEmail(testStudent.getEmail(), testStudent.getFullName(), testCourse.getTitle());
    }

    @Test
    void testUpdateProgress_AlreadyCompleted() {
        testEnrollment.setCompleted(true);
        testEnrollment.setProgress(100);

        when(enrollmentRepository.findByStudentIdAndCourseId(testStudent.getId(), testCourse.getId()))
                .thenReturn(Optional.of(testEnrollment));
        when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(testEnrollment);

        enrollmentService.updateProgress(testStudent.getId(), testCourse.getId(), 100);

        verify(enrollmentRepository).save(testEnrollment);
        verify(certificateService, never()).generateCertificate(any(), any());
        verify(notificationService, never()).sendCourseCompletionEmail(any(), any(), any());
    }

    @Test
    void testUpdateLessonProgress_FirstTimeCompletion() {
        LessonProgress lessonProgress = new LessonProgress();
        lessonProgress.setCompleted(false);

        when(lessonRepository.findById(testLesson.getId())).thenReturn(Optional.of(testLesson));
        when(lessonProgressRepository.findByStudentIdAndLessonId(testStudent.getId(), testLesson.getId()))
                .thenReturn(Optional.of(lessonProgress));
        when(userRepository.findById(testStudent.getId())).thenReturn(Optional.of(testStudent));

        // Mock for update overall progress
        when(lessonRepository.findByCourseId(testCourse.getId())).thenReturn(Collections.singletonList(testLesson));
        LessonProgress completedProgress = new LessonProgress();
        completedProgress.setCompleted(true);
        when(lessonProgressRepository.findByStudentIdAndLessonCourseId(testStudent.getId(), testCourse.getId()))
                .thenReturn(Collections.singletonList(completedProgress));

        when(enrollmentRepository.findByStudentIdAndCourseId(testStudent.getId(), testCourse.getId()))
                .thenReturn(Optional.of(testEnrollment));

        enrollmentService.updateLessonProgress(testStudent.getId(), testLesson.getId(), true);

        assertTrue(lessonProgress.isCompleted());
        verify(lessonProgressRepository).save(lessonProgress);

        // Points should be added
        assertEquals(10, testStudent.getPoints());
        verify(userRepository).save(testStudent);

        // Overall progress should be 100
        assertEquals(100, testEnrollment.getProgress());
        assertTrue(testEnrollment.isCompleted());
    }

    @Test
    void testUpdateLessonProgress_NewProgress() {
        when(lessonRepository.findById(testLesson.getId())).thenReturn(Optional.of(testLesson));
        when(lessonProgressRepository.findByStudentIdAndLessonId(testStudent.getId(), testLesson.getId()))
                .thenReturn(Optional.empty()); // Returns empty to trigger new progress creation
        when(userRepository.findById(testStudent.getId())).thenReturn(Optional.of(testStudent));

        // Mock for update overall progress
        when(lessonRepository.findByCourseId(testCourse.getId())).thenReturn(Collections.singletonList(testLesson));
        when(lessonProgressRepository.findByStudentIdAndLessonCourseId(testStudent.getId(), testCourse.getId()))
                .thenReturn(Collections.emptyList());

        when(enrollmentRepository.findByStudentIdAndCourseId(testStudent.getId(), testCourse.getId()))
                .thenReturn(Optional.of(testEnrollment));

        enrollmentService.updateLessonProgress(testStudent.getId(), testLesson.getId(), false);

        verify(lessonProgressRepository).save(any(LessonProgress.class));

        // Points should not be added (not completed)
        assertEquals(0, testStudent.getPoints());
        verify(userRepository, never()).save(testStudent);
    }

    @Test
    void testCompleteCourse() {
        when(enrollmentRepository.findByStudentIdAndCourseId(testStudent.getId(), testCourse.getId()))
                .thenReturn(Optional.of(testEnrollment));
        when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(testEnrollment);

        enrollmentService.completeCourse(testStudent.getId(), testCourse.getId());

        assertEquals(100, testEnrollment.getProgress());
        assertTrue(testEnrollment.isCompleted());
        verify(enrollmentRepository).save(testEnrollment);
    }
}
