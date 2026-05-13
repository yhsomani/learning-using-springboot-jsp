package com.ruraledu.repository;

import com.ruraledu.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {
    
    @Query("SELECT c FROM Course c WHERE c.category = :category AND c.deleted = false")
    List<Course> findByCategory(@Param("category") String category);
    
    @EntityGraph(attributePaths = {"teacher"})
    @Query("SELECT c FROM Course c WHERE c.youtubePlaylistUrl = :url AND c.deleted = false")
    Optional<Course> findByYoutubePlaylistUrl(@Param("url") String url);
    
    @Override
    @org.springframework.lang.NonNull
    Optional<Course> findById(@Param("id") @org.springframework.lang.NonNull Long id);
    
    // Recommendations: courses in same category that student hasn't enrolled in
    @Query("SELECT c FROM Course c WHERE c.category = :category AND c.deleted = false AND c.id NOT IN (SELECT e.course.id FROM Enrollment e WHERE e.student.id = :studentId)")
    List<Course> findRecommendations(@Param("category") String category, @Param("studentId") Long studentId);

    // Full-text search on title and description
    @Query("SELECT c FROM Course c WHERE c.deleted = false AND (lower(c.title) LIKE lower(concat('%', :keyword, '%')) OR lower(c.description) LIKE lower(concat('%', :keyword, '%')))")
    List<Course> searchCourses(@Param("keyword") String keyword);

    @EntityGraph(attributePaths = {"teacher"})
    @Query("SELECT c FROM Course c WHERE c.teacher.id = :teacherId AND c.deleted = false")
    List<Course> findByTeacherId(@Param("teacherId") Long teacherId);

    @Override
    @org.springframework.lang.NonNull
    List<Course> findAll();

    // New arrivals - properly limited via Pageable
    @EntityGraph(attributePaths = {"teacher"})
    @Query("SELECT c FROM Course c WHERE c.deleted = false ORDER BY c.id DESC")
    List<Course> findNewArrivals(Pageable pageable);

    @Query("SELECT count(c) FROM Course c WHERE c.deleted = false")
    long count();
}
