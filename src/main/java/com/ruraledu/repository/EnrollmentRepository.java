package com.ruraledu.repository;

import com.ruraledu.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByStudentId(Long studentId);
    Optional<Enrollment> findByStudentIdAndCourseId(Long studentId, Long courseId);
    List<Enrollment> findByCourseId(Long courseId);
    long countByCourseId(Long courseId);
    
    // HQL for SDG metrics: Total rural students (Assuming rural students have a specific role or tag, here we just count all students)
    @Query("SELECT COUNT(DISTINCT e.student.id) FROM Enrollment e")
    long countUniqueStudents();
    
    // HQL for average progress
    @Query("SELECT AVG(e.progress) FROM Enrollment e")
    Double getAverageProgress();

    @Query("SELECT c.title as courseTitle, COUNT(e.id) as studentCount FROM Course c LEFT JOIN Enrollment e ON c.id = e.course.id GROUP BY c.id, c.title")
    List<EnrollmentStatsProjection> getEnrollmentStats();
}
