package com.ruraledu.service;

import com.ruraledu.entity.Course;
import com.ruraledu.repository.CourseRepository;
import com.ruraledu.repository.LessonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    void testSearchCourses_Success() {
        // Arrange
        String keyword = "Java";
        Course course1 = new Course();
        course1.setId(1L);
        course1.setTitle("Java Basics");
        course1.setDescription("Learn Java from scratch");

        Course course2 = new Course();
        course2.setId(2L);
        course2.setTitle("Advanced Java");
        course2.setDescription("Deep dive into Java");

        List<Course> mockCourses = Arrays.asList(course1, course2);
        when(courseRepository.searchCourses(keyword)).thenReturn(mockCourses);

        // Act
        List<Course> result = courseService.searchCourses(keyword);

        // Assert
        assertEquals(2, result.size());
        assertEquals("Java Basics", result.get(0).getTitle());
        assertEquals("Advanced Java", result.get(1).getTitle());
        verify(courseRepository, times(1)).searchCourses(keyword);
    }

    @Test
    void testSearchCourses_NoResults() {
        // Arrange
        String keyword = "Python";
        when(courseRepository.searchCourses(keyword)).thenReturn(Collections.emptyList());

        // Act
        List<Course> result = courseService.searchCourses(keyword);

        // Assert
        assertTrue(result.isEmpty());
        verify(courseRepository, times(1)).searchCourses(keyword);
    }
}
