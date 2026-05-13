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
            throw new IllegalArgumentException("A course cannot be empty. It must contain at least one lesson before it can be saved.");
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
        Course course = courseRepository.findById(id).orElse(null);
        if (course != null) {
            org.hibernate.Hibernate.initialize(course.getLessons());
            if (course.getQuiz() != null) {
                org.hibernate.Hibernate.initialize(course.getQuiz().getQuestions());
            }
        }
        return course;
    }

    @Transactional(readOnly = true)
    public List<Course> getCoursesByTeacher(Long teacherId) {
        return courseRepository.findByTeacherId(teacherId);
    }
}
