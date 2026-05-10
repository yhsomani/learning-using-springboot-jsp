package com.ruraledu.service;

import com.ruraledu.entity.Course;
import com.ruraledu.entity.Lesson;
import com.ruraledu.repository.CourseRepository;
import com.ruraledu.repository.LessonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private LessonRepository lessonRepository;

    @InjectMocks
    private CourseService courseService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveCourse() {
        Course course = new Course();
        course.setTitle("Test Course");

        when(courseRepository.save(course)).thenReturn(course);

        Course savedCourse = courseService.saveCourse(course);

        assertNotNull(savedCourse);
        assertEquals("Test Course", savedCourse.getTitle());
        verify(courseRepository, times(1)).save(course);
    }

    @Test
    void testSaveCourseWithLessons() {
        Course course = new Course();
        course.setId(1L);
        course.setTitle("Test Course");

        Lesson lesson1 = new Lesson();
        Lesson lesson2 = new Lesson();
        List<Lesson> lessons = Arrays.asList(lesson1, lesson2);

        when(courseRepository.save(course)).thenReturn(course);
        when(lessonRepository.saveAll(lessons)).thenReturn(lessons);

        Course savedCourse = courseService.saveCourseWithLessons(course, lessons);

        assertNotNull(savedCourse);
        assertEquals(course, lesson1.getCourse());
        assertEquals(course, lesson2.getCourse());
        verify(courseRepository, times(1)).save(course);
        verify(lessonRepository, times(1)).saveAll(lessons);
    }

    @Test
    void testDeleteCourse() {
        Long courseId = 1L;
        Course course = new Course();
        course.setId(courseId);
        course.setDeleted(false);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        courseService.deleteCourse(courseId);

        assertTrue(course.isDeleted());
        verify(courseRepository, times(1)).findById(courseId);
        verify(courseRepository, times(1)).save(course);
    }

    @Test
    void testGetAllCourses() {
        List<Course> courses = Arrays.asList(new Course(), new Course());
        when(courseRepository.findAll()).thenReturn(courses);

        List<Course> result = courseService.getAllCourses();

        assertEquals(2, result.size());
        verify(courseRepository, times(1)).findAll();
    }

    @Test
    void testSearchCourses() {
        String keyword = "Java";
        List<Course> courses = Arrays.asList(new Course());
        when(courseRepository.searchCourses(keyword)).thenReturn(courses);

        List<Course> result = courseService.searchCourses(keyword);

        assertEquals(1, result.size());
        verify(courseRepository, times(1)).searchCourses(keyword);
    }

    @Test
    void testGetNewArrivals() {
        List<Course> courses = Arrays.asList(new Course());
        when(courseRepository.findNewArrivals(any(PageRequest.class))).thenReturn(courses);

        List<Course> result = courseService.getNewArrivals();

        assertEquals(1, result.size());
        verify(courseRepository, times(1)).findNewArrivals(any(PageRequest.class));
    }

    @Test
    void testGetRecommendations() {
        String category = "IT";
        Long studentId = 1L;
        List<Course> courses = Arrays.asList(new Course());

        when(courseRepository.findRecommendations(category, studentId)).thenReturn(courses);

        List<Course> result = courseService.getRecommendations(category, studentId);

        assertEquals(1, result.size());
        verify(courseRepository, times(1)).findRecommendations(category, studentId);
    }

    @Test
    void testGetCourseById() {
        Long id = 1L;
        Course course = new Course();
        course.setId(id);

        when(courseRepository.findById(id)).thenReturn(Optional.of(course));

        Course result = courseService.getCourseById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(courseRepository, times(1)).findById(id);
    }

    @Test
    void testGetCourseByIdNotFound() {
        Long id = 1L;
        when(courseRepository.findById(id)).thenReturn(Optional.empty());

        Course result = courseService.getCourseById(id);

        assertNull(result);
        verify(courseRepository, times(1)).findById(id);
    }

    @Test
    void testGetCoursesByTeacher() {
        Long teacherId = 1L;
        List<Course> courses = Arrays.asList(new Course());

        when(courseRepository.findByTeacherId(teacherId)).thenReturn(courses);

        List<Course> result = courseService.getCoursesByTeacher(teacherId);

        assertEquals(1, result.size());
        verify(courseRepository, times(1)).findByTeacherId(teacherId);
    }
}
