package com.ruraledu.service;

import com.ruraledu.entity.LeaderboardEntry;
import com.ruraledu.entity.User;
import com.ruraledu.repository.LeaderboardRepository;
import com.ruraledu.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    void addPoints_UserExists_PointsAdded() {
        Long userId = 1L;
        int pointsToAdd = 10;
        User user = new User();
        user.setPoints(50);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        gamificationService.addPoints(userId, pointsToAdd);

        assertEquals(60, user.getPoints());
        verify(userRepository).save(user);
    }

    @Test
    void addPoints_UserDoesNotExist_DoesNothing() {
        Long userId = 1L;
        int pointsToAdd = 10;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        gamificationService.addPoints(userId, pointsToAdd);

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateWeeklyLeaderboard_DeletesOldEntriesAndSavesNew() {
        // Arrange
        List<User> topStudents = new ArrayList<>();

        User user1 = new User();
        user1.setPoints(100);
        topStudents.add(user1);

        User user2 = new User();
        user2.setPoints(90);
        topStudents.add(user2);

        User user3 = new User();
        user3.setPoints(80);
        topStudents.add(user3);

        // We capture the exact PageRequest to verify the parameter
        ArgumentCaptor<PageRequest> pageRequestCaptor = ArgumentCaptor.forClass(PageRequest.class);
        when(userRepository.findTopStudents(pageRequestCaptor.capture())).thenReturn(topStudents);

        // Act
        gamificationService.updateWeeklyLeaderboard();

        // Assert
        verify(leaderboardRepository, times(1)).deleteAll();

        // Verify the PageRequest argument
        PageRequest capturedRequest = pageRequestCaptor.getValue();
        assertEquals(0, capturedRequest.getPageNumber());
        assertEquals(10, capturedRequest.getPageSize());

        // Capture saved LeaderboardEntry objects
        ArgumentCaptor<LeaderboardEntry> entryCaptor = ArgumentCaptor.forClass(LeaderboardEntry.class);
        verify(leaderboardRepository, times(3)).save(entryCaptor.capture());

        List<LeaderboardEntry> savedEntries = entryCaptor.getAllValues();
        assertEquals(3, savedEntries.size());

        // Check first entry
        assertEquals(user1, savedEntries.get(0).getUser());
        assertEquals(100, savedEntries.get(0).getTotalPoints());
        assertEquals(1, savedEntries.get(0).getRank());

        // Check second entry
        assertEquals(user2, savedEntries.get(1).getUser());
        assertEquals(90, savedEntries.get(1).getTotalPoints());
        assertEquals(2, savedEntries.get(1).getRank());

        // Check third entry
        assertEquals(user3, savedEntries.get(2).getUser());
        assertEquals(80, savedEntries.get(2).getTotalPoints());
        assertEquals(3, savedEntries.get(2).getRank());
    }

    @Test
    void updateWeeklyLeaderboard_NoStudents_DeletesButDoesNotSave() {
        // Arrange
        when(userRepository.findTopStudents(any(Pageable.class))).thenReturn(new ArrayList<>());

        // Act
        gamificationService.updateWeeklyLeaderboard();

        // Assert
        verify(leaderboardRepository, times(1)).deleteAll();
        verify(leaderboardRepository, never()).save(any(LeaderboardEntry.class));
    }
}
