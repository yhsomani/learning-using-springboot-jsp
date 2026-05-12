package com.ruraledu.service;

import com.ruraledu.entity.User;
import com.ruraledu.entity.LeaderboardEntry;
import com.ruraledu.repository.UserRepository;
import com.ruraledu.repository.LeaderboardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class GamificationService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LeaderboardRepository leaderboardRepository;

    @Transactional
    public void addPoints(@org.springframework.lang.NonNull Long userId, int points) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setPoints(user.getPoints() + points);
            userRepository.save(user);
        });
    }

    @Scheduled(cron = "0 0 0 * * SUN") // Every Sunday at midnight
    @Transactional
    public void updateWeeklyLeaderboard() {
        leaderboardRepository.deleteAll();
        List<User> topStudents = userRepository.findTopStudents(org.springframework.data.domain.PageRequest.of(0, 10));

        List<LeaderboardEntry> entries = new java.util.ArrayList<>();
        for (int i = 0; i < topStudents.size(); i++) {
            LeaderboardEntry entry = new LeaderboardEntry();
            entry.setUser(topStudents.get(i));
            entry.setTotalPoints(topStudents.get(i).getPoints());
            entry.setRank(i + 1);
            entries.add(entry);
        }
        leaderboardRepository.saveAll(entries);
    }
}
