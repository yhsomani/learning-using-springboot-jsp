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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    void testSearchCourses_WithResults() {
        // Arrange
        String keyword = "Java";
        Course course1 = new Course();
        course1.setId(1L);
        course1.setTitle("Java Basics");
        Course course2 = new Course();
        course2.setId(2L);
        course2.setTitle("Advanced Java");

        List<Course> expectedCourses = Arrays.asList(course1, course2);
        when(courseRepository.searchCourses(keyword)).thenReturn(expectedCourses);

        // Act
        List<Course> actualCourses = courseService.searchCourses(keyword);

        // Assert
        assertEquals(2, actualCourses.size());
        assertEquals("Java Basics", actualCourses.get(0).getTitle());
        assertEquals("Advanced Java", actualCourses.get(1).getTitle());
        verify(courseRepository).searchCourses(keyword);
    }

    @Test
    void testSearchCourses_NoResults() {
        // Arrange
        String keyword = "Python";
        when(courseRepository.searchCourses(keyword)).thenReturn(Collections.emptyList());

        // Act
        List<Course> actualCourses = courseService.searchCourses(keyword);

        // Assert
        assertTrue(actualCourses.isEmpty());
        verify(courseRepository).searchCourses(keyword);
    }

    @Test
    void testSearchCourses_NullKeyword() {
        // Arrange
        String keyword = null;
        when(courseRepository.searchCourses(keyword)).thenReturn(Collections.emptyList());

        // Act
        List<Course> actualCourses = courseService.searchCourses(keyword);

        // Assert
        assertTrue(actualCourses.isEmpty());
        verify(courseRepository).searchCourses(keyword);
    }
}
