package com.ruraledu.repository;

import com.ruraledu.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findByCourseId(Long courseId);
    
    @EntityGraph(attributePaths = {"course"})
    List<Lesson> findByCourseIdOrderByOrderIndexAsc(Long courseId);
    
    @EntityGraph(attributePaths = {"course"})
    List<Lesson> findByCourseIdOrderByOrderIndex(Long courseId);
}
