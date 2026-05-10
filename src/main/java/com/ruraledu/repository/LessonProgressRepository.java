package com.ruraledu.repository;

import com.ruraledu.entity.LessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface LessonProgressRepository extends JpaRepository<LessonProgress, Long> {
    Optional<LessonProgress> findByStudentIdAndLessonId(Long studentId, Long lessonId);
    List<LessonProgress> findByStudentIdAndLessonCourseId(Long studentId, Long courseId);
}
