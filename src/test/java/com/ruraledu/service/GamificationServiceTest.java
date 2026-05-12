package com.ruraledu.service;

import com.ruraledu.entity.LeaderboardEntry;
import com.ruraledu.entity.User;
import com.ruraledu.repository.LeaderboardRepository;
import com.ruraledu.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GamificationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private LeaderboardRepository leaderboardRepository;

    @InjectMocks
    private GamificationService gamificationService;

    @Captor
    private ArgumentCaptor<List<LeaderboardEntry>> leaderboardEntryCaptor;

    @Test
    void testAddPoints_UserExists() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setPoints(10);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        gamificationService.addPoints(userId, 20);

        assertEquals(30, user.getPoints());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testAddPoints_UserDoesNotExist() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        gamificationService.addPoints(userId, 20);

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUpdateWeeklyLeaderboard() {
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("user1");
        user1.setPoints(100);

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");
        user2.setPoints(80);

        User user3 = new User();
        user3.setId(3L);
        user3.setUsername("user3");
        user3.setPoints(50);

        List<User> topStudents = Arrays.asList(user1, user2, user3);

        when(userRepository.findTopStudents(any(PageRequest.class))).thenReturn(topStudents);

        gamificationService.updateWeeklyLeaderboard();

        // Verify deleteAll was called
        verify(leaderboardRepository, times(1)).deleteAll();

        // Verify saveAll was called with the list
        verify(leaderboardRepository, times(1)).saveAll(leaderboardEntryCaptor.capture());

        List<LeaderboardEntry> savedEntries = (List<LeaderboardEntry>) leaderboardEntryCaptor.getValue();
        assertEquals(3, savedEntries.size());

        assertEquals(user1, savedEntries.get(0).getUser());
        assertEquals(100, savedEntries.get(0).getTotalPoints());
        assertEquals(1, savedEntries.get(0).getRank());

        assertEquals(user2, savedEntries.get(1).getUser());
        assertEquals(80, savedEntries.get(1).getTotalPoints());
        assertEquals(2, savedEntries.get(1).getRank());

        assertEquals(user3, savedEntries.get(2).getUser());
        assertEquals(50, savedEntries.get(2).getTotalPoints());
        assertEquals(3, savedEntries.get(2).getRank());
    }

    @Test
    void testUpdateWeeklyLeaderboard_NoStudents() {
        when(userRepository.findTopStudents(any(PageRequest.class))).thenReturn(List.of());

        gamificationService.updateWeeklyLeaderboard();

        verify(leaderboardRepository, times(1)).deleteAll();
        verify(leaderboardRepository, never()).save(any(LeaderboardEntry.class));
    }
}
