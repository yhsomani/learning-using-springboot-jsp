package com.ruraledu;

import com.ruraledu.entity.User;
import com.ruraledu.entity.LeaderboardEntry;
import com.ruraledu.repository.UserRepository;
import com.ruraledu.repository.LeaderboardRepository;
import com.ruraledu.service.GamificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.List;
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
    void testAddPoints_UserExists() {
        Long userId = 1L;
        int pointsToAdd = 50;
        User user = new User();
        user.setId(userId);
        user.setPoints(100);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        gamificationService.addPoints(userId, pointsToAdd);

        assertEquals(150, user.getPoints());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testAddPoints_UserNotFound() {
        Long userId = 1L;
        int pointsToAdd = 50;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        gamificationService.addPoints(userId, pointsToAdd);

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUpdateWeeklyLeaderboard() {
        User user1 = new User();
        user1.setId(1L);
        user1.setPoints(200);

        User user2 = new User();
        user2.setId(2L);
        user2.setPoints(150);

        List<User> topStudents = Arrays.asList(user1, user2);
        when(userRepository.findTopStudents(PageRequest.of(0, 10))).thenReturn(topStudents);

        gamificationService.updateWeeklyLeaderboard();

        verify(leaderboardRepository, times(1)).deleteAll();

        ArgumentCaptor<LeaderboardEntry> entryCaptor = ArgumentCaptor.forClass(LeaderboardEntry.class);
        verify(leaderboardRepository, times(2)).save(entryCaptor.capture());

        List<LeaderboardEntry> savedEntries = entryCaptor.getAllValues();
        assertEquals(2, savedEntries.size());

        assertEquals(user1, savedEntries.get(0).getUser());
        assertEquals(200, savedEntries.get(0).getTotalPoints());
        assertEquals(1, savedEntries.get(0).getRank());

        assertEquals(user2, savedEntries.get(1).getUser());
        assertEquals(150, savedEntries.get(1).getTotalPoints());
        assertEquals(2, savedEntries.get(1).getRank());
    }

    @Test
    void testUpdateWeeklyLeaderboard_Empty() {
        when(userRepository.findTopStudents(PageRequest.of(0, 10))).thenReturn(java.util.Collections.emptyList());

        gamificationService.updateWeeklyLeaderboard();

        verify(leaderboardRepository, times(1)).deleteAll();
        verify(leaderboardRepository, never()).save(any(LeaderboardEntry.class));
    }
}
