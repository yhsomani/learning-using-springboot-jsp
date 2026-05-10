package com.ruraledu.service;

import com.ruraledu.entity.User;
import com.ruraledu.repository.LeaderboardRepository;
import com.ruraledu.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class GamificationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private LeaderboardRepository leaderboardRepository;

    @InjectMocks
    private GamificationService gamificationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddPoints_PositiveValue() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setPoints(10);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        gamificationService.addPoints(userId, 5);

        assertEquals(15, user.getPoints());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testAddPoints_NegativeValue() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setPoints(10);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        gamificationService.addPoints(userId, -5);

        // Based on the current implementation, points will be added (10 + (-5) = 5)
        assertEquals(5, user.getPoints());
        verify(userRepository, times(1)).save(user);
    }
}
