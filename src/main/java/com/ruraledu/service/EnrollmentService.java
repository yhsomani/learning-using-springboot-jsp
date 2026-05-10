package com.ruraledu.service;

import com.ruraledu.entity.Enrollment;
import com.ruraledu.entity.User;
import com.ruraledu.entity.Course;
import com.ruraledu.repository.EnrollmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EnrollmentService {
    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private CertificateService certificateService;

    @Transactional
    public Enrollment enroll(User student, Course course) {
        if (isEnrolled(student.getId(), course.getId())) {
            return enrollmentRepository.findByStudentIdAndCourseId(student.getId(), course.getId()).get();
        }
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setEnrollmentDate(LocalDateTime.now());
        enrollment.setProgress(0);
        enrollment.setCompleted(false);
        return enrollmentRepository.save(enrollment);
    }

    public boolean isEnrolled(Long studentId, Long courseId) {
        return enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId).isPresent();
    }

    public List<Enrollment> getStudentEnrollments(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId);
    }

    @Autowired
    private NotificationService notificationService;

    @Transactional
    public void updateProgress(Long studentId, Long courseId, int progress) {
        enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId).ifPresent(e -> {
            e.setProgress(progress);
            if (progress >= 100 && !e.isCompleted()) {
                e.setCompleted(true);
                e.setCompletionDate(LocalDateTime.now());
                certificateService.generateCertificate(e.getStudent(), e.getCourse());
                notificationService.sendCourseCompletionEmail(e.getStudent().getEmail(), e.getStudent().getFullName(), e.getCourse().getTitle());
            }
            enrollmentRepository.save(e);
        });
    }

    @Transactional
    public void updateLessonProgress(Long studentId, Long lessonId, boolean completed) {
        com.ruraledu.entity.Lesson lesson = lessonRepository.findById(lessonId).orElseThrow();
        Long courseId = lesson.getCourse().getId();
        
        com.ruraledu.entity.LessonProgress progress = lessonProgressRepository.findByStudentIdAndLessonId(studentId, lessonId)
                .orElse(new com.ruraledu.entity.LessonProgress());
        
        if (progress.getId() == null) {
            progress.setStudent(userRepository.findById(studentId).orElseThrow());
            progress.setLesson(lesson);
        }
        
        boolean wasAlreadyCompleted = progress.isCompleted();
        progress.setCompleted(completed);
        lessonProgressRepository.save(progress);
        
        // Add points if this is the first time completing the lesson
        if (completed && !wasAlreadyCompleted) {
            User student = userRepository.findById(studentId).orElseThrow();
            student.setPoints(student.getPoints() + 10);
            userRepository.save(student);
        }
        
        // Update overall course progress
        List<com.ruraledu.entity.Lesson> totalLessons = lessonRepository.findByCourseId(courseId);
        long completedCount = lessonProgressRepository.findByStudentIdAndLessonCourseId(studentId, courseId)
                .stream().filter(com.ruraledu.entity.LessonProgress::isCompleted).count();
        
        int overallProgress = totalLessons.isEmpty() ? 0 : (int) ((completedCount * 100) / totalLessons.size());
        updateProgress(studentId, courseId, overallProgress);
    }

    @Transactional
    public void completeCourse(Long studentId, Long courseId) {
        updateProgress(studentId, courseId, 100);
    }

    @Autowired
    private com.ruraledu.repository.LessonRepository lessonRepository;
    @Autowired
    private com.ruraledu.repository.LessonProgressRepository lessonProgressRepository;
    @Autowired
    private com.ruraledu.repository.UserRepository userRepository;
}
