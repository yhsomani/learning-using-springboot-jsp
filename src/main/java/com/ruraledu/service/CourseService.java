package com.ruraledu.service;

import com.ruraledu.entity.Course;
import com.ruraledu.entity.Lesson;
import com.ruraledu.repository.CourseRepository;
import com.ruraledu.repository.LessonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class CourseService {
    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Transactional
    @CacheEvict(value = "courses", allEntries = true)
    public Course saveCourse(Course course) {
        return courseRepository.save(course);
    }

    @Transactional
    @CacheEvict(value = "courses", allEntries = true)
    public Course saveCourseWithLessons(Course course, List<Lesson> lessons) {
        if (lessons == null || lessons.isEmpty()) {
            throw new IllegalArgumentException("Course must have at least one lesson.");
        }

        Course savedCourse = courseRepository.save(course);
        lessons.forEach(l -> l.setCourse(savedCourse));
        lessonRepository.saveAll(lessons);
        return savedCourse;
    }

    @Transactional
    @CacheEvict(value = "courses", allEntries = true)
    public void deleteCourse(Long id) {
        courseRepository.findById(id).ifPresent(course -> {
            course.setDeleted(true);
            courseRepository.save(course);
        });
    }

    @Cacheable(value = "courses", key = "'all'")
    @Transactional(readOnly = true)
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Course> searchCourses(String keyword) {
        return courseRepository.searchCourses(keyword);
    }

    @Transactional(readOnly = true)
    public List<Course> getNewArrivals() {
        return courseRepository.findNewArrivals(PageRequest.of(0, 5));
    }

    @Transactional(readOnly = true)
    public List<Course> getRecommendations(String category, Long studentId) {
        return courseRepository.findRecommendations(category, studentId);
    }

    @Transactional(readOnly = true)
    public Course getCourseById(@org.springframework.lang.NonNull Long id) {
        return courseRepository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<Course> getCoursesByTeacher(Long teacherId) {
        return courseRepository.findByTeacherId(teacherId);
    }
}
