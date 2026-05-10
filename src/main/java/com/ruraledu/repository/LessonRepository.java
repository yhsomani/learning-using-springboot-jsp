package com.ruraledu.repository;

import com.ruraledu.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findByCourseId(Long courseId);
    List<Lesson> findByCourseIdOrderByOrderIndexAsc(Long courseId);
}
