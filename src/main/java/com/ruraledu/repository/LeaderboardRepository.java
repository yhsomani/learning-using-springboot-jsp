package com.ruraledu.repository;

import com.ruraledu.entity.LeaderboardEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeaderboardRepository extends JpaRepository<LeaderboardEntry, Long> {
}
