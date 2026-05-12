package com.ruraledu.controller;

import com.ruraledu.entity.User;
import com.ruraledu.exception.UserAlreadyExistsException;
import com.ruraledu.repository.CourseRepository;
import com.ruraledu.repository.EnrollmentRepository;
import com.ruraledu.repository.UserRepository;
import com.ruraledu.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = MainController.class)
public class MainControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private CourseRepository courseRepository;

    @MockBean
    private EnrollmentRepository enrollmentRepository;

    @Test
    @WithMockUser
    public void testHandleRegister_UserAlreadyExists() throws Exception {
        doThrow(new UserAlreadyExistsException("User already exists"))
                .when(userService).registerUser(any(User.class));

        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("username", "testuser")
                        .param("password", "password123")
                        .param("fullName", "Test User")
                        .param("email", "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("errors"));
    }
}
