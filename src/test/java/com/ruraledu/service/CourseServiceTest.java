package com.ruraledu.service;

import com.ruraledu.entity.Course;
import com.ruraledu.repository.CourseRepository;
import com.ruraledu.repository.LessonRepository;
import com.ruraledu.service.CourseService;
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
    void testSearchCourses_ValidKeyword() {
        Course course1 = new Course();
        course1.setId(1L);
        course1.setTitle("Java Basics");

        Course course2 = new Course();
        course2.setId(2L);
        course2.setTitle("Advanced Java");

        List<Course> mockCourses = Arrays.asList(course1, course2);
        when(courseRepository.searchCourses("Java")).thenReturn(mockCourses);

        List<Course> result = courseService.searchCourses("Java");

        assertEquals(2, result.size());
        verify(courseRepository, times(1)).searchCourses("Java");
    }

    @Test
    void testSearchCourses_EmptyKeyword() {
        Course course1 = new Course();
        course1.setId(1L);

        Course course2 = new Course();
        course2.setId(2L);

        List<Course> mockCourses = Arrays.asList(course1, course2);

        // Mock the repository behavior for an empty string.
        // With %%, it matches all, so we return all mock courses.
        when(courseRepository.searchCourses("")).thenReturn(mockCourses);

        List<Course> result = courseService.searchCourses("");

        assertEquals(2, result.size());
        verify(courseRepository, times(1)).searchCourses("");
    }

    @Test
    void testSearchCourses_NullKeyword() {
        // Mock repository returning empty list for null keyword
        when(courseRepository.searchCourses(null)).thenReturn(Collections.emptyList());

        List<Course> result = courseService.searchCourses(null);

        assertTrue(result.isEmpty());
        verify(courseRepository, times(1)).searchCourses(null);
    }

    @Test
    void testSearchCourses_WhitespaceKeyword() {
        Course course1 = new Course();
        course1.setId(1L);

        List<Course> mockCourses = Collections.singletonList(course1);

        when(courseRepository.searchCourses("   ")).thenReturn(mockCourses);

        List<Course> result = courseService.searchCourses("   ");

        assertEquals(1, result.size());
        verify(courseRepository, times(1)).searchCourses("   ");
    }
}
